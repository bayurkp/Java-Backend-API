package com.bay.server;

import com.bay.data.Database;
import com.bay.data.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

public class Response {
    private final HttpExchange exchange;
    private final Database database = new Database();

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void handleGet(String tableName, String condition) throws IOException {
        Result result = this.database.select(tableName, condition);
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData().toString() +
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

        System.out.println(fieldKeys);
        System.out.println(fieldValues);

        Result result = this.database.insert(tableName, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        }

        this.send(statusCode, "{" +
                "\"status\": " + statusCode + "," +
                "\"message\": " + result.getMessage()  +
                "}");
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

        System.out.println(fieldKeys);
        System.out.println(fieldValues);

        Result result = this.database.update(tableName, id, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        }

        this.send(statusCode, "{" +
                "\"status\": " + statusCode + "," +
                "\"message\": " + result.getMessage()  +
                "}");
    }

    public void handleDelete(String tableName, int id) throws IOException {
        Result result = this.database.delete(tableName, id);
        int statusCode = result.getStatusCode();

        if (result.isSuccess()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": " + result.getMessage() + "," +
                    "\"data\": " + result.getData() +
                    "}");
        }

        this.send(statusCode, "{" +
                "\"status\": " + statusCode + "," +
                "\"message\": " + result.getMessage()  +
                "}");
    }

    public void send(int statusCode, String jsonMessage) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(statusCode, jsonMessage.length());
        outputStream.write(jsonMessage.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
