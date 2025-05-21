package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.RegisterUserInput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log
public class AuthCaller {
    private final Socket clientSocket;
    private final BufferedReader input;
    private final BufferedWriter output;
    private final ObjectMapper mapper;

    public AuthCaller(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        this.output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        this.mapper = new ObjectMapper();
    }

    public Result<Output<LoginUserOutput>> login(LoginUserInput request) {
        try {
            // Prepare login request
            Input<LoginUserInput> req = Input.<LoginUserInput>builder()
                    .command(Input.Command.LOGIN)
                    .body(request)
                    .build();
            // Serialize and send the request
            String jsonString = mapper.writeValueAsString(req);
            this.output.write(jsonString);
            this.output.newLine();
            this.output.flush();

            // Read server response
            String responseJson = input.readLine();
            log.info("Server response: " + responseJson);
            Output<LoginUserOutput> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Output<Void>> register(RegisterUserInput request) {
        try {
            // Prepare register request
            Input<RegisterUserInput> req = Input.<RegisterUserInput>builder()
                    .command(Input.Command.REGISTER)
                    .body(request)
                    .build();
            // Serialize and send the request
            String jsonString = mapper.writeValueAsString(req);
            this.output.write(jsonString);
            this.output.newLine();
            this.output.flush();

            // Read server response
            String responseJson = input.readLine();
            log.info("Server response: " + responseJson);
            Output<Void> output = mapper.readValue(responseJson,
                    new TypeReference<>() {});
            return Result.success(output);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}