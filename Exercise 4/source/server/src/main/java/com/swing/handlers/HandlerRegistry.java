package com.swing.handlers;

import com.swing.context.InputContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandlerRegistry<I, O> {
    private List<InputHandler<I, O>> inputHandlers;
    private InputContext<I, O> inputContext;

    private HandlerRegistry() {}

    public static <I, O> HandlerRegistry<I, O> withInputContext(InputContext<I, O> inputContext) {
        HandlerRegistry<I,O> handlerRegistry = new HandlerRegistry<>();
        handlerRegistry.inputContext = inputContext;
        handlerRegistry.inputHandlers = new ArrayList<>();
        return handlerRegistry;
    }

    @SuppressWarnings("unchecked")
    public HandlerRegistry<I, O> register(InputHandler<I, O>... handlers) {
        inputHandlers.addAll(Arrays.asList(handlers));
        return this;
    }

    public void handle() {
        for (InputHandler<I, O> handler : inputHandlers) {
            if (inputContext.isAborted()) return;
            handler.handle(inputContext);
        }
    }
}
