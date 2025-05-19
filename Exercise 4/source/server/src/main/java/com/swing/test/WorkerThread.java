package com.swing.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.dtos.user.LoginUserInput;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class WorkerThread extends Thread {
    private final Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info("Processing: " + socket);
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            ObjectMapper mapper = new ObjectMapper();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Received JSON: " + line);

                // Deserialize JSON to LoginUserRequest object
                LoginUserInput request = mapper.readValue(line, LoginUserInput.class);
                System.out.println("Username: " + request.getUsername());
                System.out.println("Password: " + request.getPassword());

                // Prepare a response object or string
                String response = "Hello, " + request.getUsername() + "! Login received.\n";

                // Send response back
                writer.write(response);
                writer.flush();
            }
        } catch (IOException  e) {
            System.err.println("Client disconnected or error: " + e.getMessage());
        }
        log.info("Complete processing: " + socket);
    }
}