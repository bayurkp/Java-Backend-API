package com.bay.data;

import java.sql.*;

public class Database {
    String rootPath = System.getProperty("user.dir");
    public Connection connect() {
        Connection connection = null;
        try {
            String path = "jdbc:sqlite:" + rootPath + "/ecommerce.db";
            connection = DriverManager.getConnection(path);
        } catch (SQLException e) {

            System.err.println(e.getMessage());
        }

        return connection;
    }

    public static void main(String[] args) {

        Database database =  new Database();

        try {
            Connection connection = database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("first_name") + "\t" + resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

    }
}
