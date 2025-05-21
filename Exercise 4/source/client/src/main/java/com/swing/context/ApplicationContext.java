package com.swing.context;

import com.swing.callers.AuthCaller;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.InputStream;
import java.net.Socket;

@Log
public class ApplicationContext {
    private Socket socket;
    private static ApplicationContext context;

    @Getter
    private AuthCaller authCaller;

    private ApplicationContext() {
    }

    public static Exception init(SocketConnection socketConnection) {
        context = new ApplicationContext();
        try (InputStream input = ApplicationContext.class.getClassLoader().getResourceAsStream("application.properties")) {
            context.socket = new Socket(socketConnection.getHost(), socketConnection.getPort());
            context.authCaller = new AuthCaller(context.socket);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public static ApplicationContext getInstance() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }
}
