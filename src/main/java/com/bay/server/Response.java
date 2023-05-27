package com.bay.server;

import com.bay.data.Database;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
    private final HttpExchange exchange;
    private final Database database = new Database();

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void forRequestMethodGet(String tableName, String condition) throws IOException {
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

    public void forRequestMethodPost() throws IOException {
        System.out.println(Parser.parseJson(exchange.getRequestBody()).toPrettyString());
//        try {
//            JsonNode json = Parser.parseJson(exchange.getRequestBody());
//            System.out.println(json.get("id").asInt());
//            this.send(200, json.asText());
//        } catch (Exception e) {
//            this.send(400, e.getMessage());
//        }

    }
//
//    public String put() {
//
//    }
//
//    public String delete() {
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
