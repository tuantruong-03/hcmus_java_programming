package com.swing.socket;

import com.swing.events.Event;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


@Log
public class SocketManager { //NOSONAR
    private ServerSocket serverSocket;
    private Map<String, ClientWorker> clients; // clientId - clientHandler
    private static SocketManager socketManager;


    private SocketManager() {
    }


    public static SocketManager init(int port) throws RuntimeException, IOException {
        if (socketManager != null) {
            throw new IllegalStateException("SocketManager has already been initialized");
        }
        socketManager = new SocketManager();
        socketManager.clients = new HashMap<>();
        socketManager.serverSocket = new ServerSocket(port);
        log.info("SocketManager is running on: " + socketManager.serverSocket.getInetAddress().getHostName() + ":" + socketManager.serverSocket.getLocalPort());
        return socketManager;
    }

    public void run() throws IOException {
        while (true) {
            Socket clientSocket = socketManager.serverSocket.accept();
            ClientWorker clientWorker = new ClientWorker(socketManager, clientSocket);
            Thread.startVirtualThread(clientWorker);
        }
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }

    public void keepAlive(ClientWorker clientWorker) {
        clients.put(clientWorker.getClientId(), clientWorker);
    }

    public synchronized void onEvent(Event event) {
        switch (event.getType()) {
            case Event.Type.USER_LOGIN:
                Event.LoginPayload loginPayload = (Event.LoginPayload) event.getPayload();
                log.info("SocketManager::onEvent: LOGIN: " + loginPayload.getClientId());
                for (ClientWorker clientWorker : clients.values()) {
                    if (clientWorker.getClientId().equals(loginPayload.getClientId())) continue;
                    log.info(String.format("SocketManager::onEvent LOGIN: from %s to %s", clientWorker.getClientId(), loginPayload.getClientId()));
                    Exception exception = clientWorker.onEvent(event);
                    if (exception != null) {
                        log.warning("SocketManager::onEvent: " + exception.getMessage());
                    }
                }
                break;
            case Event.Type.SEND_MESSAGE:
                Event.SendMessagePayload sendMessagePayload = (Event.SendMessagePayload) event.getPayload();
                for (ClientWorker clientWorker : clients.values()) {
                    if (sendMessagePayload.getReceiverIds().contains(clientWorker.getUserId()) || clientWorker.getUserId().equals(sendMessagePayload.getSenderId())) {
                        Exception exception = clientWorker.onEvent(event);
                        if (exception != null) {
                            log.warning("SocketManager::onEvent: " + exception.getMessage());
                        }
                    }
                }
                break;
            case Event.Type.UPDATE_MESSAGE:
                Event.UpdateMessagePayload updateMessagePayload = (Event.UpdateMessagePayload) event.getPayload();
                for (ClientWorker clientWorker : clients.values()) {
                    if (updateMessagePayload.getReceiverIds().contains(clientWorker.getUserId()) || clientWorker.getUserId().equals(updateMessagePayload.getSenderId())) {
                        Exception exception = clientWorker.onEvent(event);
                        if (exception != null) {
                            log.warning("SocketManager::onEvent: " + exception.getMessage());
                        }
                    }
                }
                break;
            case Event.Type.DELETE_MESSAGE:
                Event.DeleteMessagePayload deleteMessagePayload = (Event.DeleteMessagePayload) event.getPayload();
                for (ClientWorker clientWorker : clients.values()) {
                    if (deleteMessagePayload.getReceiverIds().contains(clientWorker.getUserId()) || clientWorker.getUserId().equals(deleteMessagePayload.getSenderId())) {
                        Exception exception = clientWorker.onEvent(event);
                        if (exception != null) {
                            log.warning("SocketManager::onEvent: " + exception.getMessage());
                        }
                    }
                }
                break;
            case Event.Type.CREATE_CHAT_ROOM:
                Event.CreateChatRoomPayload createChatRoomPayload = (Event.CreateChatRoomPayload) event.getPayload();
                for (ClientWorker clientWorker : clients.values()) {
                    if (createChatRoomPayload.getMemberIds().contains(clientWorker.getUserId())) {
                        Exception exception = clientWorker.onEvent(event);
                        if (exception != null) {
                            log.warning("SocketManager::onEvent: " + exception.getMessage());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
