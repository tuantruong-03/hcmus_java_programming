package com.swing.io.user;

import com.swing.types.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserInput {
    private String name;
    private String username;
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RegisterUserInput request;
        public Builder() {
            request = new RegisterUserInput();
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

        public Result<RegisterUserInput> build() {
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
