package com.swing.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.context.ApplicationContext;
import com.swing.context.InputContext;
import com.swing.controllers.*;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.io.message.*;
import com.swing.io.user.*;
import com.swing.events.Event;
import com.swing.models.User;
import com.swing.publishers.EventPublisher;
import com.swing.types.Result;
import com.swing.utils.TokenUtils;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Log
public class ClientWorker implements Runnable {
    @Getter
    private final String clientId;
    @Getter
    private String userId;
    private final SocketManager socketManager;
    private final Socket clientSocket;
    private final AuthHandler authHandler;
    private final ChatRoomHandler chatRoomHandler;
    private final MessageHandler messageHandler;
    private final UserHandler userHandler;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;


    public ClientWorker(SocketManager socketManager, Socket clientSocket) throws IOException {
        this.clientId = String.format("%s:%s:%s", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), System.currentTimeMillis());
        this.socketManager = socketManager;
        this.clientSocket = clientSocket;
        this.authHandler = ApplicationContext.getInstance().getAuthHandler();
        this.chatRoomHandler = ApplicationContext.getInstance().getChatRoomHandler();
        this.messageHandler = ApplicationContext.getInstance().getMessageHandler();
        this.userHandler = ApplicationContext.getInstance().getUserHandler();
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        this.eventPublisher = new EventPublisher(writer);
        this.objectMapper = ApplicationContext.getInstance().getObjectMapper();
    }

    public void run() {
        log.info("Processing: " + clientSocket);
        if (clientSocket.isClosed()) {
            socketManager.removeClient(clientId);
            return;
        }
        while (true) {
            try {
                String jsonInput = reader.readLine();
                if (jsonInput == null) {
                    socketManager.removeClient(clientId);
                    break;
                }
                log.info("Received JSON: " + jsonInput);
                var response = handleJsonInput(jsonInput);
                if (response.isFailure())
                    log.warning("failed to handle request: " + response.getException().getMessage());
                else this.write(response.getValue());
            } catch (IOException e) {
                log.warning("Client disconnected or error: " + e.getMessage());
                socketManager.removeClient(clientId);
                return;
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
                    Input<RegisterUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {
                    });
                    InputContext<RegisterUserInput, RegisterUserOutput> ctx = new InputContext<>(input);
                    return handleRegisterCommand(ctx);
                }
                case LOGIN -> {
                    Input<LoginUserInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {
                    });
                    InputContext<LoginUserInput, LoginUserOutput> ctx = new InputContext<>(input);
                    return handleLoginCommand(ctx);
                }
                case CREATE_CHATROOM -> {
                    Input<CreateChatRoomInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {
                    });
                    InputContext<CreateChatRoomInput, CreateChatRoomOutput> ctx = new InputContext<>(input);
                    return handleCreateChatRoomCommand(ctx);
                }
                case GET_MY_PROFILE -> {
                    Input<Void> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<Void, UserOutput> ctx = new InputContext<>(input);
                    return handleGetMyProfileCommand(ctx);
                }
                case GET_CHAT_ROOM -> {
                    Input<GetChatRoomInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<GetChatRoomInput, GetChatRoomOutput> ctx = new InputContext<>(input);
                    return handleGetChatRoomCommand(ctx);
                }
                case GET_MY_CHAT_ROOMS -> {
                    Input<GetChatRoomsInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<GetChatRoomsInput, GetChatRoomsOutput> ctx = new InputContext<>(input);
                    return handleGetChatRoomsCommand(ctx);
                }
                case GET_MESSAGES -> {
                    Input<GetMessagesInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<GetMessagesInput, GetMessagesOutput> ctx = new InputContext<>(input);
                    return handleGetMessagesCommand(ctx);
                }
                case SEND_MESSAGE -> {
                    Input<CreateMessageInput> input = mapper.treeToValue(rootNode, new TypeReference<>() {});
                    InputContext<CreateMessageInput, CreateMessageOutput> ctx = new InputContext<>(input);
                    return handleSendMessageCommand(ctx);
                }
                case KEEP_CONNECTION_ALIVE -> {
                    socketManager.keepAlive(this);
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
                String username = user.get().getUsername();
                String name = user.get().getName();
                Event.LoginPayload loginPayload = new Event.LoginPayload(clientId, userId, name, username);
                Event event = new Event(Event.Type.USER_LOGIN, loginPayload);
                emitEvent(event);
            }
        }
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleCreateChatRoomCommand(InputContext<CreateChatRoomInput, CreateChatRoomOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, chatRoomHandler::createOne)
                .handle();
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleSendMessageCommand(InputContext<CreateMessageInput, CreateMessageOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, messageHandler::createOne)
                .handle();
        // Emit event to SocketManager
        if (inputContext.getStatus().equals(InputContext.Status.OK)) {
            CreateMessageInput inputBody = inputContext.getInput().getBody();
            CreateMessageOutput outputBody = inputContext.getOutput().getBody();
            Event.SendMessagePayload.Content.Type type = null;
            if (inputBody.getContent() != null) {
                Content c = inputBody.getContent();
                if (c.getType().equals(Content.Type.TEXT)) {
                    type = Event.SendMessagePayload.Content.Type.TEXT;
                } else if (c.getType().equals(Content.Type.FILE)) {
                    type = Event.SendMessagePayload.Content.Type.FILE;
                }
            }
            Event.SendMessagePayload.Content content = Event.SendMessagePayload.Content.builder()
                    .type(type)
                    .value(inputBody.getContent() != null ? inputBody.getContent().getValue() : null)
                    .build();
            Event.SendMessagePayload payload = Event.SendMessagePayload.builder()
                    .messageId(outputBody.getMessageId())
                    .chatRoomId(outputBody.getChatRoomId())
                    .content(content)
                    .senderId(inputBody.getSenderId())
                    .receiverIds(inputBody.getReceiverIds())
                    .createdAt(new Date())
                    .build();
            Event event = new Event(Event.Type.SEND_MESSAGE, payload);
            emitEvent(event);
        }
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleGetMyProfileCommand(InputContext<Void, UserOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, userHandler::findMyProfile)
                .handle();
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleGetChatRoomCommand(InputContext<GetChatRoomInput, GetChatRoomOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, chatRoomHandler::findOne)
                .handle();
        return Result.success(inputContext.getOutput());

    }
    private Result<Output<?>> handleGetChatRoomsCommand(InputContext<GetChatRoomsInput, GetChatRoomsOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, chatRoomHandler::findMyChatRooms)
                .handle();
        return Result.success(inputContext.getOutput());
    }

    private Result<Output<?>> handleGetMessagesCommand(InputContext<GetMessagesInput, GetMessagesOutput> inputContext) {
        HandlerRegistry.withInputContext(inputContext)
                .register(authHandler::authenticate, messageHandler::findMany)
                .handle();
        return Result.success(inputContext.getOutput());
    }

    private void write(Object data) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        writer.write(jsonResponse);
        writer.newLine();
        writer.flush();
    }

    private void emitEvent(Event event) {
        socketManager.onEvent(event);
    }

    public Exception onEvent(Event event) {
        switch (event.getType()) {
            case USER_LOGIN, SEND_MESSAGE:
                return eventPublisher.publish(event);
            case USER_LOGOUT:
                break;
            default:
                break;
        }
        return null;
    }
}