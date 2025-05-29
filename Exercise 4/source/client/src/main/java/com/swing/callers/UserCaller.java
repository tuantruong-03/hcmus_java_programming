package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.SocketConnection;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.*;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class UserCaller {
    private final ObjectMapper mapper;
    private final SocketConnection socketConnection;

    public UserCaller(SocketConnection socketConnection, ObjectMapper mapper) {
        this.mapper =mapper;
        this.socketConnection = socketConnection;
    }

    public Result<Output<GetUserOutput>> getMyProfile() {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<Void> input = CallerUtils.INSTANCE.buildInputWithToken();
            input.setCommand(Input.Command.GET_MY_PROFILE);
            String jsonString = mapper.writeValueAsString(input);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetUserOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<GetUsersOutput>> getMany(GetUsersInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<GetUsersInput> in = CallerUtils.INSTANCE.buildInputWithToken();
            in.setCommand(Input.Command.GET_USERS );
            in.setBody(input);
            String jsonString = mapper.writeValueAsString(in);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<GetUsersOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}