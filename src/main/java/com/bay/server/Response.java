package com.bay.server;

import com.bay.data.Database;
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
        int statusCode = 200;
        String message = "OK";
        String result = this.database.select(tableName, condition);

        System.out.println(result);
        this.send(statusCode, "{" +
                "\"status\": " + statusCode + "," +
                "\"message\": " + "\"" + message + "\"," +
                "\"data\": " + result +
                "}"
        );
    }

    public void handlePost(String tableName, JsonNode jsonNode) throws IOException {
        int statusCode = 200;
        String message = "OK";

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

        fieldKeys.deleteCharAt(fieldKeys.length() - 1);
        fieldValues.deleteCharAt(fieldValues.length() - 1);

        System.out.println(fieldKeys);
        System.out.println(fieldValues);

        int result = this.database.insert(tableName, fieldKeys.toString(), fieldValues.toString());


        System.out.println(result);
        this.send(statusCode, "{" +
                "\"status\": " + statusCode + "," +
                "\"message\": " + "\"" + message + "\"," +
                "\"data\": " + result +
                "}"
        );
    }

//
//    public String handlePut() {
//
//    }
//
//    public String handleDelete() {
//
//    }

    public void send(int statusCode, String jsonMessage) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(statusCode, jsonMessage.length());
        outputStream.write(jsonMessage.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
