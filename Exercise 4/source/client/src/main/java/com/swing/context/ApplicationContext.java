package com.swing.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.callers.AuthCaller;
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
    @Getter
    private AuthCaller authCaller;

    @Getter
    private EventDispatcher eventDispatcher;

    private ApplicationContext() {
    }

    private static class Holder {
        private static ApplicationContext instance;
    }

    public static Exception init(SocketConnection socketConnection) {
        ApplicationContext instance = new ApplicationContext();
        instance.socketConnection = socketConnection;
        try (Socket socket = new Socket();){
            // Try to ping that server
            socket.connect(new InetSocketAddress(socketConnection.getHost(), socketConnection.getPort()), 500);
            instance.authCaller = new AuthCaller(socketConnection);
            Holder.instance = instance;
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
            ObjectMapper objectMapper = new ObjectMapper();
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
            this.eventDispatcher.run();
            return null;
        } catch (IOException e) {
            return e;
        }
    }
}
