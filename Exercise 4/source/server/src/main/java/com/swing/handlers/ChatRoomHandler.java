package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.dtos.Input;
import com.swing.dtos.Output;
import com.swing.dtos.chatroom.CreateChatRoomInput;
import com.swing.dtos.chatroom.CreateChatRoomOutput;
import com.swing.models.ChatRoom;
import com.swing.models.ChatRoomUser;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Log
public class ChatRoomHandler {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public ChatRoomHandler(ChatRoomRepository chatRoomRepository, ChatRoomUserRepository chatRoomUserRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomUserRepository = chatRoomUserRepository;
    }

    public void create(InputContext<CreateChatRoomInput, CreateChatRoomOutput> context) {
        Input<CreateChatRoomInput> input = context.getInput();
        CreateChatRoomInput body = input.getBody();
        String chatRoomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .name(body.getName())
                .isGroup(body.isGroup())
                .build();
        var result1 = chatRoomRepository.createOne(chatRoom);
        if (result1.isFailure()) {
            log.warning("failed to create chat room: " + result1.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<CreateChatRoomOutput>builder().error(error).build());
            return;
        }
        InputContext.Principal principal = context.getPrincipal();
        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        userIds.add(principal.getUserId());
        userIds.addAll(body.getOtherUserIds());
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
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<CreateChatRoomOutput>builder().error(error).build());
            return;
        }
        context.setOutput(Output.<CreateChatRoomOutput>builder().build());
    }
}
