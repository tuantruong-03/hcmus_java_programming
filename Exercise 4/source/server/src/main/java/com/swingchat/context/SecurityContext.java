package com.swingchat.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecurityContext {
    private Authentication authentication;

    public boolean isAuthenticated() {
        return authentication != null ;
    }
}
