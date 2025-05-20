package com.swing.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.InputContext;
import com.swing.dtos.Input;
import com.swing.dtos.Output;
import com.swing.dtos.chatroom.CreateChatRoomInput;
import com.swing.dtos.chatroom.CreateChatRoomOutput;
import com.swing.dtos.message.MessageInput;
import com.swing.dtos.message.MessageOutput;
import com.swing.dtos.user.LoginUserInput;
import com.swing.dtos.user.LoginUserOutput;
import com.swing.dtos.user.RegisterUserInput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final AuthHandler authHandler;
    private final ChatRoomHandler chatRoomHandler;
    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientHandler(Socket clientSocket
            ,AuthHandler authHandler
            ,ChatRoomHandler chatRoomHandler) throws IOException {
        this.clientSocket = clientSocket;
        this.authHandler = authHandler;
        this.chatRoomHandler = chatRoomHandler;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void run() {
        log.info("Processing: " + clientSocket);
        while (true) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonInput = in.readLine();
                log.info("Received JSON: " + jsonInput);
                var response = handleJsonInput(jsonInput);
                if (response.isFailure()) {
                    log.warning("failed to handle request: " + response.getException().getMessage());
                } else {
                    String jsonResponse = mapper.writeValueAsString(response.getValue());
                    out.write(jsonResponse);
                    out.newLine();
                    out.flush();
                }
            } catch (IOException e) {
                log.warning("Client disconnected or error: " + e.getMessage());
            }
            if (clientSocket.isClosed()) {
                break;
            }
        }
    }

    private Result<Output<?>> handleJsonInput(String jsonInput) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonInput);
            String useCaseStr = rootNode.get("command").asText();
            if (Input.Command.REGISTER.toString().equals(useCaseStr)) {
                Input<RegisterUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                InputContext<RegisterUserInput,Void> inputContext = new InputContext<>(input);
                HandlerRegistry.withInputContext(inputContext).register(authHandler::register).handle();
                return Result.success(inputContext.getOutput());

            } else if (Input.Command.LOGIN.toString().equals(useCaseStr)) {
                Input<LoginUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                InputContext<LoginUserInput,LoginUserOutput> inputContext = new InputContext<>(input);
                HandlerRegistry.withInputContext(inputContext).register(authHandler::login).handle();
                return Result.success(inputContext.getOutput());

            } else if (Input.Command.CREATE_CHATROOM.toString().equals(useCaseStr)) {
                Input<CreateChatRoomInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                InputContext<CreateChatRoomInput, CreateChatRoomOutput> inputContext = new InputContext<>(input);
                HandlerRegistry.withInputContext(inputContext).register(authHandler::validate, chatRoomHandler::create).handle();
                return Result.success(inputContext.getOutput());

            }
            return Result.failure(new IllegalArgumentException("Invalid UseCase: " + useCaseStr));
        } catch (ClassCastException e) {
            return Result.failure(new IllegalArgumentException("Invalid request type: " + e.getMessage()));
        } catch (RuntimeException | IOException e) {
            return Result.failure(new IllegalArgumentException("Invalid JSON: " + e.getMessage()));
        }
    }
}