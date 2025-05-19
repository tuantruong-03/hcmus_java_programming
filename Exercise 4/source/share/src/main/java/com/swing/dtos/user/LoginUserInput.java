package com.swing.dtos.user;

import com.swing.types.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserInput {
    private String username;
    private String password;

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private final LoginUserInput request;
        public Builder() {
            request = new LoginUserInput();
        }
        public Builder username(String username) {
            request.username = username;
            return this;
        }
        public Builder password(String password) {
            request.password = password;
            return this;
        }
        public Result<LoginUserInput> build() {
            if (StringUtils.isBlank(request.username)) {
                return Result.failure(new IllegalArgumentException("Username must not be empty or null"));
            }
            if (request.username.length() < 6) {
                return Result.failure(new IllegalArgumentException("Username must be at least 6 characters long"));
            }
            if (Character.isDigit(request.username.charAt(0))) {
                return Result.failure(new IllegalArgumentException("Username must not start with a number"));
            }
            if (request.username.contains(" ")) {
                return Result.failure(new IllegalArgumentException("Username must not contain spaces"));
            }
            if (StringUtils.isBlank(request.password) || request.password.length() < 6) {
                return Result.failure(new IllegalArgumentException("Password must be at least 6 characters long"));
            }
            if (request.password.contains(" ")) {
                return Result.failure(new IllegalArgumentException("Password must not contain spaces"));
            }
            return Result.success(request);
        }
    }
}
