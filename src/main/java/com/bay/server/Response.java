package com.bay.server;

import com.bay.data.Database;
import com.bay.data.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Response {
    private final HttpExchange exchange;
    private final Database database = new Database();

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void handleGet(String tableName, String condition) throws IOException {
        Result result = database.select(tableName, condition);
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}"
            );
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() +
                    "}"
            );
        }
    }

    public void handleGet(String tableMaster, int id, String tableDetail) throws IOException, SQLException {
        Result resultParent = database.select(tableMaster, "id=" + id);
        String jsonResult = resultParent.getData();

        int statusCode = resultParent.getStatusCode();
        boolean isSuccess = resultParent.isSuccess();
        String message = resultParent.getMessage();

        if (!isSuccess) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + message +
                    "}");
            return;
        }

        if (tableMaster.equals("users")) {
            if (tableDetail == null) {
                Result addresses = database.select("addresses", "user=" + id);
                isSuccess = addresses.isSuccess();
                if (!isSuccess) {
                    statusCode = addresses.getStatusCode();
                    message = addresses.getMessage();
                }
                jsonResult = database.joinJson(jsonResult,
                        "addresses", addresses.getData());
            } else if (tableDetail.equals("products")) {
                Result products = database.select("products", "seller=" + id);
                isSuccess = products.isSuccess();
                if (!isSuccess) {
                    statusCode = products.getStatusCode();
                    message = products.getMessage();
                }
                jsonResult = database.joinJson(jsonResult,
                        "products", products.getData());
            } else if (tableDetail.equals("orders")) {
                Result orders = database.select("orders", "buyer=" + id);
                isSuccess = orders.isSuccess();
                if (!isSuccess) {
                    statusCode = orders.getStatusCode();
                    message = orders.getMessage();
                }
                jsonResult = database.joinJson(jsonResult,
                        "orders", orders.getData());
            } else if (tableDetail.equals("reviews")) {
                String query = "SELECT id FROM orders WHERE buyer=" + id;
                Connection connection = database.connect();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                ArrayList<Integer> idOrders = new ArrayList<>();
                while (resultSet.next()) {
                    idOrders.add(resultSet.getInt("id"));
                }

                Result reviews = new Result();

                if (idOrders.size() == 1) {
                    reviews = database.select("reviews", "`order`=" + idOrders.get(0));
                } else {
                    ArrayList<Object> tempReviews = new ArrayList<>();
                    for (Integer idOrder : idOrders) {
                        tempReviews.add(database.select("reviews", "`order`=" + idOrder).getData());
                    }

                    reviews.setData(tempReviews);
                }
                jsonResult = database.joinJson(jsonResult,
                        "reviews", reviews.getData());
            }
        } else if (tableMaster.equals("orders") && tableDetail == null) {
            // Select ID Buyer
            String query = "SELECT buyer FROM orders WHERE id=" + id;
            Connection connection = database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            int idBuyer = resultSet.getInt("buyer");

            Result buyer = database.select("users", "id=" + idBuyer);
            jsonResult = database.joinJson(jsonResult,
                    "buyer", buyer.getData());

            // Select ID Product
            Result orderDetail = database.customSelect("SELECT products.title, orderDetails.* FROM orderDetails " +
                    "JOIN products ON orderDetails.product = products.id " +
                    "WHERE orderDetails.`order`=" + 2);
            jsonResult = database.joinJson(jsonResult,
                    "orderDetails", orderDetail.getData());

            Result review = database.select("reviews", "`order`=" + id);
            jsonResult = database.joinJson(jsonResult,
                    "reviews", review.getData());
        } else if (tableMaster.equals("products") && tableDetail == null) {
            String query = "SELECT seller FROM products WHERE id=" + id;
            Connection connection = database.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            int idSeller = resultSet.getInt("seller");

            Result users = database.select("users", "`id`=" + idSeller);
            jsonResult = database.joinJson(jsonResult,
                    "seller", users.getData());
        }

        if (!isSuccess) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + message +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + message + "," +
                    "\"data\": " + jsonResult +
                    "}"
            );
        }
    }

    public void handlePost(String tableName, JsonNode jsonNode) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            fieldKeys.append(field.getKey());
            fieldKeys.append(",");

            fieldValues.append(field.getValue());
            fieldValues.append(",");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);
        fieldValues.deleteCharAt(fieldValues.length() - 1);

        Result result = database.insert(tableName, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage()  +
                    "}");
        }
    }

    public void handlePut(String tableName, int id, JsonNode jsonNode) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            fieldKeys.append(field.getKey());
            fieldKeys.append(",");

            fieldValues.append(field.getValue());
            fieldValues.append(",");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);
        fieldValues.deleteCharAt(fieldValues.length() - 1);

        Result result = database.update(tableName, id, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage()  +
                    "}");
        }
    }

    public void handleDelete(String tableName, int id) throws IOException {
        Result result = database.delete(tableName, id);
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage()  +
                    "}");
        }
    }

    public void send(int statusCode, String jsonMessage) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(statusCode, jsonMessage.length());
        outputStream.write(jsonMessage.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
