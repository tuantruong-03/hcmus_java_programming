package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.io.Output;
import com.swing.io.user.*;
import com.swing.repository.UserRepository;
import lombok.extern.java.Log;

@Log
public class UserHandler {
    private final UserRepository userRepository;
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void findMyProfile(InputContext<Void, UserOutput> context) {
        InputContext.Principal principal = context.getPrincipal();
        if (principal == null) {
            context.setAborted(true);
            context.setAuthenticated(false);
            return;
        }
        UserOutput userOutput = UserOutput.builder()
                .id(principal.getUserId())
                .name(principal.getName())
                .username(principal.getUsername())
                .build();
        context.setOutput(Output.<UserOutput>builder()
                        .body(userOutput)
                .build());
    }

}
