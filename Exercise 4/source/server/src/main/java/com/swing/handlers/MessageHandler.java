package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.CreateChatRoomInput;
import com.swing.io.chatroom.CreateChatRoomOutput;
import com.swing.io.message.*;
import com.swing.mapper.MessageContentMapper;
import com.swing.models.ChatRoom;
import com.swing.models.ChatRoomUser;
import com.swing.models.Message;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import com.swing.repository.MessageRepository;
import com.swing.types.Result;
import com.swing.utils.FileUtils;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log
public class MessageHandler {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public MessageHandler(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository, ChatRoomUserRepository chatRoomUserRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomUserRepository = chatRoomUserRepository;
    }


    public void createOne(InputContext<CreateMessageInput, CreateMessageOutput> context) {
        Input<CreateMessageInput> input = context.getInput();
        CreateMessageInput body = input.getBody();
        String chatRoomId = body.getChatRoomId();
        if (chatRoomId == null) {
            var result1 = CreateChatRoomInput.builder().otherUserIds(body.getReceiverIds().toArray(new String[0])).build();
            if (result1.isFailure()) {
                log.warning("failed to create message: " + result1.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
                context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
                return;
            }
            var result2 = createChatRoomIfNotExist(context.getPrincipal().getUserId(), result1.getValue());
            if (result2.isFailure()) {
                log.warning("failed to create message: " + result2.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
                context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
                return;
            }
            chatRoomId = result2.getValue().getChatRoomId();
        }
        Content content = body.getContent();
        Message.Content messageContent = MessageContentMapper.fromIOToModel(content);
        if (content.getType() == Content.Type.FILE ) {
            String fileName = body.getContent().getFileName();
            byte[] fileData = body.getContent().getFileData();
            String path = String.format("files/chat_rooms/%s/%s_%s", chatRoomId  , System.currentTimeMillis(), fileName);
            var result = FileUtils.store(fileData, path);
            if (result.isFailure()) {
                log.warning("failed to create message: " + result.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
                context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
                return;
            }
            messageContent.setValue(path);
        }
        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .chatRoomId(chatRoomId)
                .senderId(body.getSenderId())
                .content(messageContent)
                .build();
        var result = messageRepository.createOne(message);
        if (result.isFailure()) {
            log.warning("failed to create message: " + result.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
            return;
        }
        String messageId = message.getId();
        CreateMessageOutput createMessageOutput = CreateMessageOutput.builder()
                .messageId(messageId)
                .chatRoomId(chatRoomId)
                .build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<CreateMessageOutput>builder()
                .body(createMessageOutput)
                .build());
    }

    private Result<CreateChatRoomOutput> createChatRoomIfNotExist(String myUserId, CreateChatRoomInput input) {
        String chatRoomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .name(myUserId + input.getOtherUserIds().getFirst())
                .isGroup(false)
                .build();
        var result1 = chatRoomRepository.createOne(chatRoom);
        if (result1.isFailure()) {
            log.warning("failed to create chat room: " + result1.getException().getMessage());
            return Result.failure(result1.getException());
        }
        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        userIds.add(myUserId);
        userIds.addAll(input.getOtherUserIds());
        for (String userId : userIds) {
            ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                    .chatRoomId(chatRoomId)
                    .userId(userId)
                    .build();
            chatRoomUsers.add(chatRoomUser);
        }
        var result2 = chatRoomUserRepository.createMany(chatRoomUsers);
        if (result2.isFailure()) {
            log.warning("failed to create chat room: " + result2.getException().getMessage());
            return Result.failure(result2.getException());
        }
        return Result.success(CreateChatRoomOutput.builder()
                .chatRoomId(chatRoomId)
                .build());
    }

    public void findOne(InputContext<GetMessageInput, GetMessageOutput> context) {
        Input<GetMessageInput> input = context.getInput();
        GetMessageInput body = input.getBody();
        var result = messageRepository.findOne(MessageRepository.Query.builder()
                .messageId(body.getMessageId())
                .chatRoomId(body.getChatRoomId())
                .build());
        if (result.isFailure()) {
            log.warning("MessageHandler::findOne: " + result.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<GetMessageOutput>builder().error(error).build());
        }
        Message message = result.getValue();
        Content content = MessageContentMapper.fromModelToIO(message.getContent());
        if (content.getType() == Content.Type.FILE) {
            try {
                String path = message.getContent().getValue();
                Path p = Paths.get(path);
                String fullFileName = p.getFileName().toString();
                String fileName = fullFileName.substring(fullFileName.indexOf("_") + 1);
                byte[] fileData = FileUtils.readBytes(path);
                content.setFileData(fileData);
                content.setFileName(fileName);
            } catch (IOException e) {
                log.warning("MessageHandler::findOne: " + e.getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
                context.setOutput(Output.<GetMessageOutput>builder().error(error).build());
                return;
            }
        }
        GetMessageOutput output = GetMessageOutput.builder()
                .messageId(body.getMessageId())
                .chatRoomId(body.getChatRoomId())
                .content(content)
                .build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<GetMessageOutput>builder().body(output).build());
    }

    public void findMany(InputContext<GetMessagesInput, GetMessagesOutput> context) {
        Input<GetMessagesInput> input = context.getInput();
        GetMessagesInput body = input.getBody();
        var result = messageRepository.findMany(MessageRepository.Query.builder()
                        .chatRoomId(body.getChatRoomId())
                .page(body.getPage())
                .limit(body.getLimit())
                .build());
        if (result.isFailure()) {
            log.warning("MessageHandler::findMany: " + result.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<GetMessagesOutput>builder().error(error).build());
            return;
        }
        List<Message> messages = result.getValue();
        List<GetMessagesOutput.Item> items = messages.stream()
                .map(message -> GetMessagesOutput.Item.builder()
                        .messageId(message.getId())
                        .chatRoomId(message.getChatRoomId())
                        .senderId(message.getSenderId())
                        .content(MessageContentMapper.fromModelToIO(message.getContent()))
                        .createdAt(message.getCreatedAt())
                        .updatedAt(message.getUpdatedAt())
                        .build())
                .toList();
        GetMessagesOutput outputBody = GetMessagesOutput.builder()
                .items(items)
                .build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<GetMessagesOutput>builder()
                .body(outputBody)
                .build());
    }

    public void update(InputContext<UpdateMessageInput, UpdateMessageOutput> context) {
        Input<UpdateMessageInput> input = context.getInput();
        UpdateMessageInput body = input.getBody();
        MessageRepository.UpdatePayload payload = MessageRepository.UpdatePayload.builder()
                .content(MessageContentMapper.fromIOToModel(body.getContent()))
                .build();
        MessageRepository.Query query = MessageRepository.Query.builder()
                .messageId(body.getMessageId())
                .chatRoomId(body.getChatRoomId())
                .build();
        var result = messageRepository.update(payload, query);
        if (result.isFailure()) {
            log.warning("MessageHandler::update: " + result.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<UpdateMessageOutput>builder().error(error).build());
            return;
        }
        UpdateMessageOutput output = UpdateMessageOutput.builder().build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<UpdateMessageOutput>builder()
                .body(output)
                .build());
    }

    public void delete(InputContext<DeleteMessageInput, DeleteMessageOutput> context) {
        Input<DeleteMessageInput> input = context.getInput();
        DeleteMessageInput body = input.getBody();
        MessageRepository.Query query = MessageRepository.Query.builder()
                .messageId(body.getMessageId())
                .chatRoomId(body.getChatRoomId())
                .build();
        var findResult = messageRepository.findOne(query);
        if (findResult.isFailure()) {
            log.warning("MessageHandler::delete: " + findResult.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<DeleteMessageOutput>builder().error(error).build());
            return;
        }
        Message message = findResult.getValue();
        if (message.getContent().getType().equals(Content.Type.FILE)) {
            var deleteFileResult = FileUtils.delete(message.getContent().getValue());
            if (deleteFileResult.isFailure()) {
                log.warning("MessageHandler::delete: " + deleteFileResult.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
                context.setOutput(Output.<DeleteMessageOutput>builder().error(error).build());
                return;
            }
        }
        var result = messageRepository.delete(query);
        if (result.isFailure()) {
            log.warning("MessageHandler::delete: " + result.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<DeleteMessageOutput>builder().error(error).build());
            return;
        }
        DeleteMessageOutput output = DeleteMessageOutput.builder().build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<DeleteMessageOutput>builder()
                .body(output)
                .build());
    }
}
