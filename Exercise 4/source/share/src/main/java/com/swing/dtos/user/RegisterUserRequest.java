package com.swing.dtos.user;

import com.swing.types.Result;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class RegisterUserRequest {
    private String name;
    private String username;
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RegisterUserRequest request;
        public Builder() {
            request = new RegisterUserRequest();
        }
        public Builder name(String name) {
            request.name = name;
            return this;
        }

        public Builder username(String username) {
            request.username = username;
            return this;
        }

        public Builder password(String password) {
            request.password = password;
            return this;
        }

        public Result<RegisterUserRequest> build() {
            if (StringUtils.isBlank(request.name)) {
                return Result.failure(new IllegalArgumentException("Name must not be empty or null"));
            }
            if (StringUtils.isBlank(request.username)) {
                return Result.failure(new IllegalArgumentException("Username must not be empty or null"));
            }
            if (request.username.length() < 6) {
                return Result.failure(new IllegalArgumentException("Username must be at least 6 characters long"));
            }
            if (Character.isDigit(request.username.charAt(0))) {
                return Result.failure(new IllegalArgumentException("Username must not start with a number"));
            }
            if (StringUtils.isBlank(request.password) || request.password.length() < 6) {
                return Result.failure(new IllegalArgumentException("Password must be at least 6 characters long"));
            }

            return Result.success(request);
        }
    }
}
