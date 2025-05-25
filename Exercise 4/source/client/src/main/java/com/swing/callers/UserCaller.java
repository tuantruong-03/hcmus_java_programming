package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.SocketConnection;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.RegisterUserInput;
import com.swing.io.user.UserOutput;
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

    public Result<Output<UserOutput>> getMyProfile() {
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
            Output<UserOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}