package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class ConnectDb {
    private static final String URL = System.getenv("URL");
    private static final String USERNAME = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static DSLContext getDSL() throws SQLException {
        Connection conn = getConnection();
        return DSL.using(conn, SQLDialect.MYSQL);
    }
}
