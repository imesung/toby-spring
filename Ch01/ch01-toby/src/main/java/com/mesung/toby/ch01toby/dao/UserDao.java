package com.mesung.toby.ch01toby.dao;

import com.mesung.toby.ch01toby.domain.User;

import java.sql.*;

/**
 * @author wglee21g@gmail.com
 */
public class UserDao {
    private ConnectionMaker connectionMaker;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    /*public UserDao(){}

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }*/

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeNewConnection();
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");

        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();
        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeNewConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        ps.executeUpdate();
        ps.close();
        c.close();

        return user;
    }

    public void delete(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeNewConnection();
        PreparedStatement ps = c.prepareStatement("delete from USERS where id = ?");

        ps.setString(1, id);

        ps.executeUpdate();

        ps.close();
        c.close();
    }
}
