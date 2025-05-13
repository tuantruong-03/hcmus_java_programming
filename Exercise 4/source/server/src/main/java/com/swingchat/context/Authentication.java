package com.swingchat.context;

import com.swingchat.types.Result;
import lombok.Getter;

@Getter
public class Authentication {
    private final Principal principal;
    private final Credential credential;
    private Authentication(Principal principal, Credential credential) {
        this.principal = principal;
        this.credential = credential;
    }

    public static Result<Authentication> init(Principal principal, Credential credential) {
        if (principal == null || credential == null) {
            return Result.failure(new IllegalStateException("require principal and credential to be set"));
        }
        return Result.success(new Authentication(principal, credential));
    }
}
