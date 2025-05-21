package com.swing.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.ApplicationContext;
import com.swing.context.InputContext;
import com.swing.context.SocketManager;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.CreateChatRoomInput;
import com.swing.io.chatroom.CreateChatRoomOutput;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.RegisterUserInput;
import com.swing.events.Event;
import com.swing.io.user.RegisterUserOutput;
import com.swing.models.User;
import com.swing.publishers.EventPublisher;
import com.swing.types.Result;
import com.swing.utils.TokenUtils;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Log
public class ClientHandler implements Runnable {
    @Getter
    private final String clientId;
    @Getter
    private String userId;
    private final SocketManager socketManager;
    private final Socket clientSocket;
    private final AuthHandler authHandler;
    private final ChatRoomHandler chatRoomHandler;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;


    public ClientHandler(String clientId, SocketManager socketManager, Socket clientSocket) throws IOException {
        this.clientId = clientId;
        this.socketManager = socketManager;
        this.clientSocket = clientSocket;
        this.authHandler = ApplicationContext.getInstance().getAuthHandler();
        this.chatRoomHandler = ApplicationContext.getInstance().getChatRoomHandler();
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        this.eventPublisher = new EventPublisher(out);
        this.objectMapper = new ObjectMapper();
    }

    public void run() {
        log.info("Processing: " + clientSocket);
        while (true) {
            try {
                String jsonInput = in.readLine();
                log.info("Received JSON: " + jsonInput);
                var response = handleJsonInput(jsonInput);
                if (response.isFailure()) {
                    log.warning("failed to handle request: " + response.getException().getMessage());
                    continue;
                }
                this.write(response.getValue());
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

            switch (Input.Command.valueOf(useCaseStr)) {
                case REGISTER -> {
                    Input<RegisterUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<RegisterUserInput, RegisterUserOutput> ctx = new InputContext<>(input);
                    return handleRegisterCommand(ctx);
                }
                case LOGIN -> {
                    Input<LoginUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<LoginUserInput, LoginUserOutput> ctx = new InputContext<>(input);
                    return handleLoginCommand(ctx);
                }
                case CREATE_CHATROOM -> {
                    Input<CreateChatRoomInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<CreateChatRoomInput, CreateChatRoomOutput> ctx = new InputContext<>(input);
                    return handleCreateChatRoomCommand(ctx);
                }
                case SEND_MESSAGE -> {
                    return Result.success(new Output<>());
                }
                default -> {
                    return Result.failure(new IllegalArgumentException("Unsupported command: " + useCaseStr));
                }
            }
        } catch (ClassCastException e) {
            return Result.failure(new IllegalArgumentException("Invalid request type: " + e.getMessage()));
        } catch (RuntimeException | IOException e) {
            return Result.failure(new IllegalArgumentException("Invalid JSON: " + e.getMessage()));
        }
    }


    private Result<Output<?>> handleRegisterCommand(InputContext<RegisterUserInput, RegisterUserOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext).register(authHandler::register).handle();
        return Result.success(inputContext.getOutput());
    }
    private Result<Output<?>> handleLoginCommand(InputContext<LoginUserInput, LoginUserOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext).register(authHandler::login).handle();
        if (inputContext.getOutput().getError() == null) {
            String token = inputContext.getOutput().getBody().getToken();
            Optional<User> user = TokenUtils.getUser(token);
            if (user.isPresent()) {
                this.userId = user.get().getId();
                Event.LoginPayload loginPayload = new Event.LoginPayload(clientId, userId);
                Event event = new Event(Event.Type.LOGIN, loginPayload);
                emitEvent(event);
            }
        }
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleCreateChatRoomCommand(InputContext<CreateChatRoomInput, CreateChatRoomOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::validate, chatRoomHandler::create)
                .handle();
        return Result.success(inputContext.getOutput());
    }

    private void write(Object data) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        out.write(jsonResponse);
        out.newLine();
        out.flush();
    }

    private void emitEvent(Event event) {
        socketManager.onEvent(event);
    }

    public void onEvent(Event event) {
        try {
        switch (event.getType()) {
            case LOGIN, SEND_MESSAGE:
                    eventPublisher.publish(event);
              break;
            case LOGOUT:
                break;
            default:
                break;
        }
        } catch (IOException e) {
            log.warning("publish event: " + e.getMessage());
        }
    }
}