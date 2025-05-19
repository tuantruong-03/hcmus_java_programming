package com.swing.handlers;

import com.swing.context.InputContext;
import com.swing.dtos.message.MessageInput;
import com.swing.dtos.message.MessageOutput;

import com.swing.repository.UserRepository;
import lombok.extern.java.Log;


@Log
public class MessageHandler {
    private final UserRepository userRepository;
    public MessageHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void send(InputContext<MessageInput, MessageOutput> inputContext) {

    }

}
