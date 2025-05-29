package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.io.Input;
import com.swing.io.Output;
import com.swing.io.user.*;
import com.swing.models.User;
import com.swing.repository.UserRepository;
import lombok.extern.java.Log;

import java.util.List;

@Log
public class UserHandler {
    private final UserRepository userRepository;
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void findMyProfile(InputContext<Void, GetUserOutput> context) {
        InputContext.Principal principal = context.getPrincipal();
        if (principal == null) {
            context.setAborted(true);
            context.setAuthenticated(false);
            return;
        }
        GetUserOutput getUserOutput = GetUserOutput.builder()
                .id(principal.getUserId())
                .name(principal.getName())
                .username(principal.getUsername())
                .build();
        context.setOutput(Output.<GetUserOutput>builder()
                        .body(getUserOutput)
                .build());
    }
    public void findMany(InputContext<GetUsersInput, GetUsersOutput> context) {
        Input<GetUsersInput> input = context.getInput();
        GetUsersInput body = input.getBody();
        var result1 = userRepository.findMany(UserRepository.Query.builder()
                        .inUserIds(body.getInUserIds())
                        .page(0)
                .build());
        if (result1.isFailure()) {
            log.warning("failed to findMembers: " + result1.getException().getMessage());
            Output.Error error = Output.Error.interalServerError();
            context.setStatus(InputContext.Status.INTERNAL_SERVER_ERROR);
            context.setOutput(Output.<GetUsersOutput>builder().error(error).build());
            return;
        }
        List<User> users = result1.getValue();
        List<GetUsersOutput.Item> items = users.stream()
                .map(u -> GetUsersOutput.Item.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .username(u.getUsername())
                        .build())
                .toList();
        context.setStatus(InputContext.Status.OK);
        context.setOutput(Output.<GetUsersOutput>builder()
                .body(GetUsersOutput.builder().items(items).build())
                .build());
    }
}
