package com.mesung.toby.ch01toby.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        //return new UserDao(new DConnectionMaker());
        //return new UserDao(connectionMaker());
        UserDao userDao = new UserDao();
        //userDao.setConnectionMaker(connectionMaker());
        userDao.setDataSource(dataSource());
        return userDao;
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

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        //dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/mesung");
        dataSource.setUsername("spring");
        dataSource.setPassword("book");

        return dataSource;

    }
}
