package com.mesung.toby.ch01toby.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException;
}
