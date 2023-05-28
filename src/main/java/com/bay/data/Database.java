package com.bay.data;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Result select(String tableName, String condition) {
        List<String> rows = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + tableName +
                    (condition != null ? " WHERE " + condition : "");
            Connection connection = this.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                // Format to json
                StringBuilder row = new StringBuilder();
                row.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    row.append("\"").append(columnName).append("\": \"").append(columnValue).append("\",");
                }
                row.deleteCharAt(row.length() - 1);
                row.append("}");
                // End of formatting

                ObjectMapper objectMapper = new ObjectMapper();
                Object object = objectMapper.readValue(row.toString(), Class.forName("com.bay.data." + getClassName(tableName)));
                rows.add(object.toString());
            }

            if (rows.size() == 0) return new Result(null, "No matching data found, please check your request", 404, false);
            return new Result(rows, "Success", 200, true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, e.getMessage(), 400, false);
        }
    }

    public Result insert(String tableName, String fieldKeys, String fieldValues) {
        try {
            String query = "INSERT INTO " + tableName + " (" + fieldKeys + ") " + "VALUES (" + fieldValues + ") ";
            Connection connection = this.connect();
            Statement statement = connection.createStatement();
            return new Result(statement.executeUpdate(query), "Success", 200,true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, e.getMessage(), 404,false);
        }
    }

    public String getClassName(String tableName) {
        StringBuilder stringBuilder = new StringBuilder(tableName);
        stringBuilder.deleteCharAt(0);
        stringBuilder.insert(0, tableName.toUpperCase().charAt(0));

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        if (stringBuilder.charAt(stringBuilder.length() - 1) == 'e') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public String[] getTables() {
        return tables;
    }

    public static void main(String[] args) {
    }
}
