package com.swing.dtos;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private Error error;
    private Map<String, String> metadata;
    private T body;

    @Builder
    @Getter
    @NoArgsConstructor
    public static class Error {
        private int code;
        private String message;
        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public static Error interalServerError() {
            return new Error(500, "Internal Server Error");
        }

        public static Error badRequest(String message) {
            return new Error(400, message);
        }
    }

}
