package com.bay.data;

import java.sql.*;
import java.util.*;

public class Database {
    private final String rootPath = System.getProperty("user.dir");
    private final String[] tables = {
            "addresses",
            "orderDetails",
            "orders",
            "products",
            "reviews",
            "users"
    };
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

    public String[] getTables() {
        return tables;
    }

    public String select(String tableName, String condition) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + tableName +
                    (condition != null ? " WHERE " + condition : "");
            Connection connection = this.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    if (columnValue instanceof String) row.put("\"" + columnName + "\"", "\"" + columnValue + "\"");
                    else row.put("\"" + columnName + "\"", columnValue);
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
            return e.getMessage();
        }

        return rows.toString().replaceAll("=", ": ");
    }

//    public String insert(String tableName, String fields, String records) {
//
//    }

    public static void main(String[] args) {

    }

}
