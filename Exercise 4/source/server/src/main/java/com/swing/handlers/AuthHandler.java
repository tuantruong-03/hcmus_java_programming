package com.swing.handlers;

import com.swing.dtos.Request;
import com.swing.dtos.Response;
import com.swing.dtos.user.LoginUserRequest;
import com.swing.dtos.user.LoginUserResponse;
import com.swing.dtos.user.RegisterUserRequest;
import com.swing.models.User;
import com.swing.repository.UserRepository;
import com.swing.types.Result;
import com.swing.utils.TokenUtils;
import lombok.extern.java.Log;

import java.util.UUID;

@Log
public class AuthHandler {
    private final UserRepository userRepository;
    public AuthHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<Void> register(Request<RegisterUserRequest> request) {
        RegisterUserRequest body = request.getBody();
        Result<Boolean> doesExist = userRepository.doesExist(UserRepository.Query.builder()
                        .username(body.getUsername())
                .build());
        if (doesExist.isFailure()) {
            log.warning("failed to register: " + doesExist.getException().getMessage());
            Response.Error error = Response.Error.interalServerError();
            return Response.<Void>builder().error(error).build();
        }
        if (Boolean.TRUE.equals(doesExist.getValue())) {
            Response.Error error = Response.Error.builder()
                    .code(400)
                    .message("Username already exists")
                    .build();
            return Response.<Void>builder().error(error).build();
        }
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(body.getName())
                .username(body.getUsername())
                .password(body.getPassword())
                .build();
        Result<Void> result = userRepository.createOne(user);
        if (result.isFailure()) {
            log.warning("failed to register: " + result.getException().getMessage());
            Response.Error error = Response.Error.interalServerError();
            return Response.<Void>builder().error(error).build();
        }
        return Response.<Void>builder().build();
    }

    public Response<LoginUserResponse> login(Request<LoginUserRequest> request) {
        LoginUserRequest body = request.getBody();
        Result<User> user = userRepository.findOne(UserRepository.Query.builder()
                        .username(body.getUsername())
                        .password(body.getPassword())
                .build());
        if (user.isFailure()) {
            log.warning("failed to login: " + user.getException().getMessage());
            Response.Error error = Response.Error.interalServerError();
            return Response.<LoginUserResponse>builder().error(error).build();
        }
        if (user.getValue() == null) {
            Response.Error error = Response.Error.badRequest("The combination of username and password is incorrect");
            return Response.<LoginUserResponse>builder().error(error).build();
        }
        String token = TokenUtils.register(user.getValue());
        return Response.<LoginUserResponse>builder()
                        .body(LoginUserResponse.builder().token(token).build())
                .build();
    }
}
