package com.swing.context;

import com.swing.events.Event;
import com.swing.handlers.ClientHandler;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


@Log
public class SocketManager {
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clients; // clientId - clientHandler
    private static SocketManager socketManager;


    private SocketManager() {
    }

    public static SocketManager init() throws RuntimeException {
        if (socketManager != null) {
            throw new RuntimeException("SocketManager has already been initialized");
        }
        socketManager = new SocketManager();
        socketManager.clients = new HashMap<>();
        try (InputStream input = SocketManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            props.load(input);
            String port = props.getProperty("server.port");
            socketManager.serverSocket = new ServerSocket(Integer.parseInt(port));

        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException("Failed to initialize SocketContext", e);
        }
        return socketManager;
    }

    public void run() throws IOException {
        while (true) {
            Socket clientSocket = socketManager.serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socketManager, clientSocket);
            Thread.startVirtualThread(clientHandler);
        }
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }

    public void keepAlive(ClientHandler clientHandler) {
        clients.put(clientHandler.getClientId(), clientHandler);
    }

    public void onEvent(Event event) {
        switch (event.getType()) {
            case Event.Type.LOGIN:
                Event.LoginPayload loginPayload = (Event.LoginPayload) event.getPayload();
                for (ClientHandler clientHandler : clients.values()) {
                    if (clientHandler.getClientId().equals(loginPayload.getClientId())) continue;
                    clientHandler.onEvent(event);
                }
                break;
            case Event.Type.SEND_MESSAGE:
                Event.SendMessagePayload sendMessagePayload = (Event.SendMessagePayload) event.getPayload();
                for (ClientHandler clientHandler : clients.values()) {
                    if (sendMessagePayload.getReceiverIds().contains(clientHandler.getClientId())) {
                        clientHandler.onEvent(event);
                    }
                }
                break;
            default:
                break;
        }
    }
}
