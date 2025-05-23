package com.swing.controllers;

import com.swing.context.InputContext;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.models.ChatRoom;
import com.swing.models.ChatRoomUser;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ChatRoomHandler {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public ChatRoomHandler(ChatRoomRepository chatRoomRepository, ChatRoomUserRepository chatRoomUserRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomUserRepository = chatRoomUserRepository;
    }

    public void findOne(InputContext<GetChatRoomInput, GetChatRoomOutput> context) {
        Input<GetChatRoomInput> input = context.getInput();
        GetChatRoomInput body = input.getBody();
        var result1 = chatRoomRepository.findOne(ChatRoomRepository.Query.builder()
                .chatRoomId(body.getChatRoomId())
                .build());
        if (result1.isFailure()) {
            log.warning("failed to findChatRoom: " + result1.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<GetChatRoomOutput>builder().error(error).build());
            return;
        }
        ChatRoom chatRoom = result1.getValue();
        if (chatRoom == null) {
            log.warning("failed to findChatRoom: " + result1.getException().getMessage());
            Output.Error error = Output.Error.badRequest("chatroom not found");
            context.setOutput(Output.<GetChatRoomOutput>builder().error(error).build());
            return;
        }

        var result2 = chatRoomUserRepository.findMany(ChatRoomUserRepository.Query.builder()
                .chatRoomId(body.getChatRoomId())
                .build());
        if (result2.isFailure()) {
            log.warning("failed to findMyChatRooms: " + result2.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<GetChatRoomOutput>builder().error(error).build());
            return;
        }
        List<ChatRoomUser> chatRoomUsers = result2.getValue();
        Map<String, String> members = new HashMap<>();
        for (ChatRoomUser chatRoomUser : chatRoomUsers) {
            // temp
            members.put(chatRoomUser.getUserId(), "");
        }

        GetChatRoomOutput getChatRoomsOutput = GetChatRoomOutput.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .members(members)
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
        Output<GetChatRoomOutput> output = Output.<GetChatRoomOutput>builder()
                .body(getChatRoomsOutput)
                .build();
        context.setOutput(output);
    }

    public void findMyChatRooms(InputContext<GetChatRoomsInput, GetChatRoomsOutput> context) {
        Input<GetChatRoomsInput> input = context.getInput();
        GetChatRoomsInput body = input.getBody();
        InputContext.Principal principal = context.getPrincipal();
        var result1 = chatRoomUserRepository.findMany(ChatRoomUserRepository.Query.builder()
                .userId(principal.getUserId())
                .limit(body.getLimit())
                .page(body.getPage())
                .build());
        if (result1.isFailure()) {
            log.warning("failed to findMyChatRooms: " + result1.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<GetChatRoomsOutput>builder().error(error).build());
            return;
        }
        List<ChatRoomUser> chatRoomUsers = result1.getValue();
        List<String> chatRoomIds = new ArrayList<>();
        for (ChatRoomUser chatRoomUser : chatRoomUsers) {
            chatRoomIds.add(chatRoomUser.getChatRoomId());
        }
        var result2 = chatRoomRepository.findMany(ChatRoomRepository.Query.builder()
                .inChatRoomIds(chatRoomIds)
                .build());
        if (result2.isFailure()) {
            log.warning("failed to findMyChatRooms: " + result2.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setOutput(Output.<GetChatRoomsOutput>builder().error(error).build());
            return;
        }
        List<ChatRoom> chatRooms = result2.getValue();
        List<GetChatRoomsOutput.Item> items = chatRooms.stream()
                .map((chatRoom -> GetChatRoomsOutput.Item
                        .builder()
                        .id(chatRoom.getId())
                        .name(chatRoom.getName())
                        .isGroup(chatRoom.getIsGroup())
                        .createdAt(chatRoom.getCreatedAt())
                        .updatedAt(chatRoom.getUpdatedAt())
                        .build()))
                .toList();
        GetChatRoomsOutput getChatRoomsOutput = GetChatRoomsOutput.builder()
                .page(body.getPage())
                .items(items)
                .build();
        Output<GetChatRoomsOutput> output = Output.<GetChatRoomsOutput>builder()
                .body(getChatRoomsOutput)
                .build();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(output);
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
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<CreateChatRoomOutput>builder()
                .body(CreateChatRoomOutput.builder().chatRoomId(chatRoomId).build())
                .build());
    }
}
