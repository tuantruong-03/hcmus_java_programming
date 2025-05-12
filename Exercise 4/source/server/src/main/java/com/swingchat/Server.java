package com.swingchat;

import com.swingchat.context.ApplicationContext;
import com.swingchat.models.User;
import com.swingchat.repository.UserRepository;

import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Server {
    public static void main(String[] args) {
        ApplicationContext.init();
        ApplicationContext.getInstance().getUserRepository().createOne(User.builder()
                        .id(UUID.randomUUID().toString())
                        .name("tuan truong")
                        .username("tuan.truong@email.com")
                        .password("123")
                        .build());

        ApplicationContext.getInstance().getUserRepository().findMany(
                UserRepository.Query.builder()
                        .username("tuan.truong05")
                        .password("123")
                        .name("tuan truong")
                        .build()
        );
    }
}