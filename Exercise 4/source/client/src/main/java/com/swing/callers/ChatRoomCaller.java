package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.SocketConnection;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class ChatRoomCaller {
    private final ObjectMapper mapper;
    private final SocketConnection socketConnection;

    public ChatRoomCaller(SocketConnection socketConnection, ObjectMapper mapper) {
        this.mapper = mapper;
        this.socketConnection = socketConnection;
    }


    public Result<Output<CreateChatRoomOutput>> createOne(CreateChatRoomInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<CreateChatRoomInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.CREATE_CHATROOM);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<CreateChatRoomOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetChatRoomsOutput>> getMyChatRooms(GetChatRoomsInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetChatRoomsInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_MY_CHAT_ROOMS);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetChatRoomsOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetChatRoomOutput>> getChatRoom(GetChatRoomInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetChatRoomInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_CHAT_ROOM);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetChatRoomOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<CheckChatRoomExistenceOutput>> checkChatRoomExistence(CheckChatRoomExistenceInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<CheckChatRoomExistenceInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.CHECK_CHAT_ROOM_EXISTENCE);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<CheckChatRoomExistenceOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetChatRoomMembersOutput>> getChatRoomMembers(GetChatRoomMembersInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetChatRoomMembersInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_CHAT_ROOM_MEMBERS);
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetChatRoomMembersOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

}
