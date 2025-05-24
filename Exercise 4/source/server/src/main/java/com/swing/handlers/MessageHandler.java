package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.CreateChatRoomInput;
import com.swing.io.chatroom.CreateChatRoomOutput;
import com.swing.io.message.CreateMessageInput;
import com.swing.io.message.CreateMessageOutput;
import com.swing.models.ChatRoom;
import com.swing.models.ChatRoomUser;
import com.swing.models.Message;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import com.swing.repository.MessageRepository;
import com.swing.types.Result;
import lombok.extern.java.Log;

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
            var result1 = CreateChatRoomInput.builder().otherUserIds(body.getReceiverId()).build();
            if (result1.isFailure()) {
                log.warning("failed to create message: " + result1.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
                return;
            }
            var result2 = createChatRoomIfNotExist(context.getPrincipal().getUserId(), result1.getValue());
            if (result2.isFailure()) {
                log.warning("failed to create message: " + result2.getException().getMessage());
                Output.Error error = Output.Error.interalServerError();
                context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
                return;
            }
            chatRoomId = result2.getValue().getChatRoomId();
        }
        CreateMessageInput.Content content = body.getContent();
        Message.Content.Type type = null;
        if (content.getType() != null) {
            if (content.getType() == CreateMessageInput.Content.Type.TEXT) {
                type = Message.Content.Type.TEXT;
            } else if (content.getType() == CreateMessageInput.Content.Type.FILE) {
                type = Message.Content.Type.FILE;
            }
        }
        Message.Content messageContent = Message.Content.builder()
                .value(content.getValue())
                .type(type)
                .build();
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
            context.setOutput(Output.<CreateMessageOutput>builder().error(error).build());
            return;
        }
        CreateMessageOutput createMessageOutput = CreateMessageOutput.builder()
                .chatRoomId(chatRoomId)
                .build();
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
}
