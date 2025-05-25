package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.SocketConnection;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.RegisterUserInput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class AuthCaller {
    private final ObjectMapper mapper;
    private final SocketConnection socketConnection;

    public AuthCaller(SocketConnection socketConnection, ObjectMapper mapper) {
        this.mapper = mapper;
        this.socketConnection = socketConnection;
    }

    public Result<Output<LoginUserOutput>> login(LoginUserInput request) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<LoginUserInput> req = Input.<LoginUserInput>builder()
                    .command(Input.Command.LOGIN)
                    .body(request)
                    .build();
            // Serialize and send the request
            String jsonString = mapper.writeValueAsString(req);
            writer.write(jsonString);
            writer.newLine();writer.flush();
            // Read server response
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<LoginUserOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<Void>> register(RegisterUserInput input) {
        try (Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            Input<RegisterUserInput> req = Input.<RegisterUserInput>builder()
                    .command(Input.Command.REGISTER)
                    .body(input)
                    .build();
            String jsonString = mapper.writeValueAsString(req);
            writer.write(jsonString);
            writer.newLine();
            writer.flush();

            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            Output<Void> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}