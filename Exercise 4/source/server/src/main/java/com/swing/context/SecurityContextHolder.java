package com.swing.context;

public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> contextHolder = ThreadLocal.withInitial(SecurityContext::new);

    private SecurityContextHolder() {}

    public static SecurityContext getContext() {
        return contextHolder.get();
    }

    public static void setContext(SecurityContext context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}