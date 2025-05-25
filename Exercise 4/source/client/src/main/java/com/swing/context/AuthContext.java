package com.swing.context;

import lombok.Getter;

public class AuthContext {
    public static final AuthContext INSTANCE = new AuthContext();
    private  Principal principal;

    public void setPrincipal(Principal p) {
        principal = p;
    }
    public Principal getPrincipal() {
        return principal;
    }

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
        private final String username;
        public Principal(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }
}
