package com.mesung.toby.ch01toby.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        //return new UserDao(new DConnectionMaker());
        return new UserDao(connectionMaker());
    }

    @Bean
    public AccountDao accountDao() {
        //return new AccountDao(new DConnectionMaker());
        return new AccountDao(connectionMaker());
    }

    @Bean
    public MessageDao messageDao() {
        //return new MessageDao(new DConnectionMaker());
        return new MessageDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
