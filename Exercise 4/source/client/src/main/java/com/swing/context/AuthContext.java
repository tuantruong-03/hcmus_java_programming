package com.swing.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthContext {
    public static final AuthContext INSTANCE = new AuthContext();
    private  Principal principal;

    public boolean isAuthenticated() {
        return principal != null;
    }
    public boolean isAnonymous() {
        return principal == null;
    }

    public void clearPrincipal() {
        principal = null;
    }

    @Getter
    public static class Principal {
        private final String userId;
        private final String name;
        private final String username;
        public Principal(String userId, String name, String username) {
            this.userId = userId;
            this.name = name;
            this.username = username;
        }
    }
}
