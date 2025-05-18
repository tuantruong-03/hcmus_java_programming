package com.swing.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.dtos.Request;
import com.swing.dtos.Response;
import com.swing.dtos.user.LoginUserRequest;
import com.swing.dtos.user.RegisterUserRequest;
import com.swing.types.Result;

import java.io.IOException;

public class RequestHandler {
    private final AuthHandler authHandler;

    public RequestHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public Result<Response<?>> handleJsonInput(String jsonInput) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonInput);
            String useCaseStr = rootNode.get("useCase").asText();
            // Step 3: Determine the use case and parse accordingly
            if (Request.UseCase.REGISTER.toString().equals(useCaseStr)) {
                Request<RegisterUserRequest> request = mapper.treeToValue(rootNode, new TypeReference<Request<RegisterUserRequest>>() {});
                return Result.success(authHandler.register(request));
            } else if (Request.UseCase.LOGIN.toString().equals(useCaseStr)) {
                Request<LoginUserRequest> request = mapper.treeToValue(rootNode, new TypeReference<Request<LoginUserRequest>>() {});
                return Result.success(authHandler.login(request));
            }

            return Result.failure(new IllegalArgumentException("Invalid UseCase: " + useCaseStr));
        } catch (ClassCastException e) {
            return Result.failure(new IllegalArgumentException("Invalid request type: " + e.getMessage()));
        } catch (RuntimeException | IOException e) {
            return Result.failure(new IllegalArgumentException("Invalid JSON: " + e.getMessage()));
        }
    }
}
