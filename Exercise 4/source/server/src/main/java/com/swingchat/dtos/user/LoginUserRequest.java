package com.swingchat.dtos.user;

import com.swingchat.types.Result;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class LoginUserRequest {
    private String username;
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LoginUserRequest request;
        public Builder() {
            request = new LoginUserRequest();
        }
        public Builder username(String username) {
            request.username = username;
            return this;
        }
        public Builder password(String password) {
            request.password = password;
            return this;
        }
        public Result<LoginUserRequest> build() {
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
