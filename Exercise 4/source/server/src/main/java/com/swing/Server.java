package com.swing;

import com.swing.context.ApplicationContext;
import com.swing.context.SocketContext;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Server {
    public static void main(String[] args) {
        ApplicationContext applicationContext = ApplicationContext.init();
        SocketContext.init(applicationContext);

//        ApplicationContext.getInstance().getUserRepository().createOne(User.builder()
//                        .id(UUID.randomUUID().toString())
//                        .name("tuan truong")
//                        .username("tuan.truong@email.com")
//                        .password("123")
//                        .build());
//
//        ApplicationContext.getInstance().getUserRepository().findMany(
//                UserRepository.Query.builder()
//                        .username("tuan.truong05")
//                        .password("123")
//                        .name("tuan truong")
//                        .build()
//        );
    }
}