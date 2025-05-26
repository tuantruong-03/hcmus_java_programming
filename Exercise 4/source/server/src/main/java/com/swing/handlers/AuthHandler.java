package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.RegisterUserInput;
import com.swing.io.user.RegisterUserOutput;
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

    public void register(InputContext<RegisterUserInput, RegisterUserOutput> inputContext) {
       Input<RegisterUserInput> input = inputContext.getInput();
        RegisterUserInput body = input.getBody();
        Result<Boolean> doesExist = userRepository.doesExist(UserRepository.Query.builder()
                        .username(body.getUsername())
                .build());
        if (doesExist.isFailure()) {
            log.warning("failed to register: " + doesExist.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            inputContext.setOutput(Output.<RegisterUserOutput>builder().error(error).build());
        }
        if (Boolean.TRUE.equals(doesExist.getValue())) {
            Output.Error error = Output.Error.builder()
                    .code(Output.Error.Code.BAD_REQUEST)
                    .message("Username already exists")
                    .build();
            inputContext.setOutput(Output.<RegisterUserOutput>builder().error(error).build());
            return;
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
            inputContext.setOutput(Output.<RegisterUserOutput>builder().error(error).build());
            return;
        }
        inputContext.setStatus(InputContext.Status.OK);
        inputContext.setOutput(Output.<RegisterUserOutput>builder()
                        .body(RegisterUserOutput.builder().build())
                .build());
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
            return;
        }
        String token = TokenUtils.register(user.getValue());
        inputContext.setStatus(InputContext.Status.OK);
        inputContext.setOutput(Output.<LoginUserOutput>builder()
                .body(LoginUserOutput.builder().token(token).build())
                .build());
    }

    public void authenticate(InputContext<?,?> inputContext) {
        String token = inputContext.getToken();
        Optional<User> user = TokenUtils.getUser(token);
        if (user.isEmpty()) {
            inputContext.setAborted(true);
            inputContext.setAuthenticated(false);
            return;
        }
        inputContext.setAuthenticated(true);
        inputContext.setStatus(InputContext.Status.OK);
        InputContext.Principal principal = InputContext.Principal.builder()
                .userId(user.get().getId())
                .name(user.get().getName())
                .username(user.get().getUsername())
                .build();
        inputContext.setPrincipal(principal);
    }
}
