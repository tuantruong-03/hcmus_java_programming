package com.swing.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.callers.AuthCaller;
import com.swing.callers.UserCaller;
import com.swing.event.EventDispatcher;
import com.swing.io.Input;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


@Log
public class ApplicationContext {

    private SocketConnection socketConnection;
    private ObjectMapper objectMapper;
    @Getter
    private AuthCaller authCaller;
    @Getter
    private UserCaller userCaller;

    @Getter
    private EventDispatcher eventDispatcher;

    private ApplicationContext() {
    }

    private static class Holder {
        private static ApplicationContext instance;
    }

    public static Exception init(SocketConnection socketConnection) {
        ApplicationContext context = new ApplicationContext();
        context.socketConnection = socketConnection;
        context.objectMapper = new ObjectMapper();
        try (Socket socket = new Socket();){
            // Try to ping that server
            socket.connect(new InetSocketAddress(socketConnection.getHost(), socketConnection.getPort()), 500);
            context.authCaller = new AuthCaller(socketConnection, context.objectMapper);
            context.userCaller = new UserCaller(socketConnection, context.objectMapper);
            Holder.instance = context;
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public static ApplicationContext getInstance() {
        if (Holder.instance == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return Holder.instance;
    }

    public Exception runEventDispatcher() {
        try {
            Socket clientSocket = new Socket(socketConnection.getHost(), socketConnection.getPort());
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            Input<?> req = Input.builder()
                    .command(Input.Command.KEEP_CONNECTION_ALIVE)
                    .build();
            // Serialize and send the request
            String jsonString = objectMapper.writeValueAsString(req);
            writer.write(jsonString);
            writer.newLine();
            writer.flush();
            // Read server response
            String responseJson = reader.readLine();
            log.info("Server response: " + responseJson);
            this.eventDispatcher = new EventDispatcher(reader);
            Thread thread = Thread.startVirtualThread(this.eventDispatcher);
            log.info("Started thread: " + thread);
            return null;
        } catch (IOException e) {
            return e;
        }
    }
}
