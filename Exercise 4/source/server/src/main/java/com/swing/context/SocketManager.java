package com.swing.context;

import com.swing.handlers.ClientHandler;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


@Log
public class SocketManager {
    private ServerSocket serverSocket;
    private ApplicationContext applicationContext;

    private static SocketManager socketManager;

    private SocketManager() {
    }

    public static SocketManager init(ApplicationContext applicationContext) throws RuntimeException {
        if (socketManager != null) {
            throw new RuntimeException("SocketManager has already been initialized");
        }
        socketManager = new SocketManager();
        socketManager.applicationContext = applicationContext;
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
            Socket socket = socketManager.serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket
                    , applicationContext.getAuthHandler()
                    , applicationContext.getChatRoomHandler());
            new Thread(clientHandler).start();
        }
    }
}
