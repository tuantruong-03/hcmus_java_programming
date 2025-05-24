package com.swing.controllers;

import com.swing.context.InputContext;

@FunctionalInterface
public interface InputHandler<I, O> {
    void handle(InputContext<I, O> context);
}
