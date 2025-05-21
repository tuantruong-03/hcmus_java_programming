package com.swing.context;

import com.swing.io.Input;
import com.swing.io.Output;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class InputContext<I, O> {
    private Input<I> input;
    private Map<String, Object> contextMap;
    private Output<O> output;
    private boolean isAuthenticated;
    private boolean isAborted;

    public InputContext(Input<I> input) {
        this.input = input;
        this.output = new Output<>();
        this.contextMap = new HashMap<>();
        this.isAuthenticated = false;
        this.isAborted = false;
    }

    public String getToken() {
        return contextMap.get("token").toString();
    }

    public Principal getPrincipal() {
        return (Principal) contextMap.get("principal");
    }

    public void setPrincipal(Principal principal) {
        contextMap.put("principal", principal);
    }

    @Builder
    @Getter
    public static class Principal {
        private String username;
        private String userId;
    }
}
