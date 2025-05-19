package com.swing.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.dtos.user.LoginUserInput;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    public final static String SERVER_IP = "127.0.0.1";
    public final static int SERVER_PORT = 7;

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Connected to server");

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            ObjectMapper mapper = new ObjectMapper();

            // Example: send 3 login requests
            for (int i = 1; i <= 3; i++) {
                LoginUserInput login = new LoginUserInput("user" + i, "pass" + i);

                // Serialize object to JSON string
                String jsonString = mapper.writeValueAsString(login);

                // Send JSON + newline (message delimiter)
                writer.write(jsonString);
                writer.newLine();
                writer.flush();

                // Read server response line
                String response = reader.readLine();
                System.out.println("Server response: " + response);

                Thread.sleep(500);
            }
        } finally {
            socket.close();
        }
    }
}
