package com.bay.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public void start() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:ecommerce.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException exception) {
            System.err.println(exception.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }
}
