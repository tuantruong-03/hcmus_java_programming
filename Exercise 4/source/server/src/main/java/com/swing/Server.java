package com.swing;

import com.swing.context.ApplicationContext;
import com.swing.socket.SocketManager;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Server {
    public static void main(String[] args) throws Exception {
        ApplicationContext.init();
        SocketManager.init().run();
    }
}