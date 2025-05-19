package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.dtos.Input;
import com.swing.dtos.Output;
import com.swing.dtos.user.LoginUserInput;
import com.swing.dtos.user.LoginUserOutput;
import com.swing.dtos.user.RegisterUserInput;
import com.swing.models.User;
import com.swing.repository.UserRepository;
import com.swing.types.Result;
import com.swing.utils.TokenUtils;
import lombok.extern.java.Log;

import java.util.Optional;
import java.util.UUID;

@Log
public class AuthHandler {
    private final UserRepository userRepository;
    public AuthHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(InputContext<RegisterUserInput, Void> inputContext) {
       Input<RegisterUserInput> input = inputContext.getInput();
        RegisterUserInput body = input.getBody();
        Result<Boolean> doesExist = userRepository.doesExist(UserRepository.Query.builder()
                        .username(body.getUsername())
                .build());
        if (doesExist.isFailure()) {
            log.warning("failed to register: " + doesExist.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            inputContext.setOutput(Output.<Void>builder().error(error).build());
        }
        if (Boolean.TRUE.equals(doesExist.getValue())) {
            Output.Error error = Output.Error.builder()
                    .code(400)
                    .message("Username already exists")
                    .build();
            inputContext.setOutput(Output.<Void>builder().error(error).build());
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
            Output.Error error = Output.Error.interalServerError();
            inputContext.setOutput(Output.<Void>builder().error(error).build());

        }
        inputContext.setOutput(Output.<Void>builder().build());
    }

    public void login(InputContext<LoginUserInput, LoginUserOutput> inputContext) {
        Input<LoginUserInput> input = inputContext.getInput();
        LoginUserInput body = input.getBody();
        Result<User> user = userRepository.findOne(UserRepository.Query.builder()
                        .username(body.getUsername())
                        .password(body.getPassword())
                .build());
        if (user.isFailure()) {
            log.warning("failed to login: " + user.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            inputContext.setOutput(Output.<LoginUserOutput>builder().error(error).build());
        }
        if (user.getValue() == null) {
            Output.Error error = Output.Error.badRequest("The combination of username and password is incorrect");
            inputContext.setOutput(Output.<LoginUserOutput>builder().error(error).build());

        }
        String token = TokenUtils.register(user.getValue());
        inputContext.setOutput(Output.<LoginUserOutput>builder()
                .body(LoginUserOutput.builder().token(token).build())
                .build());
    }

    public void validate(InputContext inputContext) {
        String token = inputContext.getToken();
        Optional<User> user = TokenUtils.getUser(token);
        if (user.isEmpty()) {
            inputContext.setAborted(true);
            inputContext.setAuthenticated(false);
            return;
        }
        inputContext.setAuthenticated(true);
        InputContext.Principal principal = InputContext.Principal.builder()
                .username(user.get().getUsername())
                .build();
        inputContext.setPrincipal(principal);
    }
}
