package com.swing.context;

import lombok.Getter;

@Getter
public class SocketConnection {
    private final String host;
    private final int port;
    public SocketConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

}
