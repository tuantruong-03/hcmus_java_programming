package com.swing.services;

import com.swing.dtos.user.LoginUserRequest;
import com.swing.dtos.user.RegisterUserRequest;
import com.swing.models.User;
import com.swing.repository.UserRepository;
import com.swing.types.Result;
import com.swing.utils.TokenUtils;

import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<Void> register(RegisterUserRequest request) {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        return userRepository.createOne(user);
    }

    public Result<String> login(LoginUserRequest request) {
        Result<User> user = userRepository.findOne(UserRepository.Query.builder()
                        .username(request.getUsername())
                        .password(request.getPassword())
                .build());
        if (user.isFailure()) {
            return Result.failure(user.getException());
        }
        String token = TokenUtils.register(user.getValue());
        return Result.success(token);
    }
}
