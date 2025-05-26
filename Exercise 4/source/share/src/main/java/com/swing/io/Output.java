package com.swing.io;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Output<T> {
    private Error error;
    private Map<String, String> metadata;
    private T body;

    @Builder
    @Getter
    @NoArgsConstructor
    public static class Error {
        private Code code;
        private String message;
        public Error(Code code, String message) {
            this.code = code;
            this.message = message;
        }

        public static Error interalServerError() {
            return new Error(Code.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

        public static Error badRequest(String message) {
            return new Error(Code.BAD_REQUEST, message);
        }

        public static Error notFound(String message) {
            return new Error(Code.NOT_FOUND, message);
        }

        public enum Code {
            BAD_REQUEST,
            NOT_FOUND,
            INTERNAL_SERVER_ERROR
        }
    }

}
