package com.swing.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.handlers.RequestHandler;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Log
public class SocketContext {
    private ServerSocket serverSocket;

    private SocketContext() {}

    public static void init(ApplicationContext applicationContext) throws RuntimeException   {
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
                new Thread(new Client(socket, applicationContext.getRequestHandler())).start();
            }

        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException("Failed to initialize SocketContext", e);
        }
    }

    private static class Client implements Runnable {
        private final Socket clientSocket;
        private final RequestHandler requestHandler;
        private final BufferedReader in;
        private final BufferedWriter out;

        public Client(Socket clientSocket, RequestHandler requestHandler) throws IOException {
            this.clientSocket = clientSocket;
            this.requestHandler = requestHandler;
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
                    var response = requestHandler.handleJsonInput(jsonInput);
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
    }

}
