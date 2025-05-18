package com.swing.callers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.dtos.Request;
import com.swing.dtos.Response;
import com.swing.dtos.user.LoginUserRequest;
import com.swing.dtos.user.LoginUserResponse;
import com.swing.dtos.user.RegisterUserRequest;
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

    public Result<Response<LoginUserResponse>> login(LoginUserRequest request) {
        try {
            // Prepare login request
            Request<LoginUserRequest> req = Request.<LoginUserRequest>builder()
                    .useCase(Request.UseCase.LOGIN)
                    .body(request)
                    .build();
            // Serialize and send the request
            String jsonString = mapper.writeValueAsString(req);
            output.write(jsonString);
            output.newLine();
            output.flush();

            // Read server response
            String responseJson = input.readLine();
            log.info("Server response: " + responseJson);
            Response<LoginUserResponse> response = mapper.readValue(responseJson,
                    new TypeReference<Response<LoginUserResponse>>() {});
            return Result.success(response);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }

    public Result<Response<Void>> register(RegisterUserRequest request) {
        try {
            // Prepare register request
            Request<RegisterUserRequest> req = Request.<RegisterUserRequest>builder()
                    .useCase(Request.UseCase.REGISTER)
                    .body(request)
                    .build();
            // Serialize and send the request
            String jsonString = mapper.writeValueAsString(req);
            output.write(jsonString);
            output.newLine();
            output.flush();

            // Read server response
            String responseJson = input.readLine();
            log.info("Server response: " + responseJson);
            Response<Void> response = mapper.readValue(responseJson,
                    new TypeReference<Response<Void>>() {});
            return Result.success(response);
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
}