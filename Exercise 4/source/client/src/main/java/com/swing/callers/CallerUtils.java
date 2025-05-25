package com.swing.callers;

import com.swing.io.Input;

import java.util.HashMap;
import java.util.Map;

public class CallerUtils {
    public static final CallerUtils INSTANCE = new CallerUtils();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    private CallerUtils() { }

    public <I> Input<I> buildInputWithToken() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("token", token);
        return Input.<I>builder()
                .metadata(metadata)
                .build();
    }
}
