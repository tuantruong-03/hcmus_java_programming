package com.swing.context;

import com.swing.handlers.ClientHandler;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


@Log
public class SocketContext {
    private ServerSocket serverSocket;

    private SocketContext() {
    }

    public static void init(ApplicationContext applicationContext) throws RuntimeException {
        SocketContext context = new SocketContext();
        try (InputStream input = SocketContext.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            props.load(input);
            String port = props.getProperty("server.port");
            context.serverSocket = new ServerSocket(Integer.parseInt(port));
            while (true) {
                Socket socket = context.serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket
                        , applicationContext.getAuthHandler()
                        , applicationContext.getMessageHandler());
                new Thread(clientHandler).start();
            }

        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException("Failed to initialize SocketContext", e);
        }
    }
}
