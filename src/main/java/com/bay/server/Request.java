package com.bay.server;

import com.bay.data.Database;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Request {
    private final String[] requestMethodsAllowed = {
      "POST",
      "GET",
      "PUT",
      "DELETE"
    };
    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
            String[] requestPath = new Request().splitRequest(exchange.getRequestURI().getPath(), "/");
            String tableName = requestPath[0];
            String requestMethod = exchange.getRequestMethod();

            if (!new Request().isRequestMethodAllowed(requestMethod)) {
                new Response(exchange).send(405, "{" +
                        "\"status\": 405," +
                        "\"message\": \"Method not allowed\"" +
                        "}");
            }

            if (!new Request().isTableExists(tableName)) {
                new Response(exchange).send(404, "{" +
                        "\"status\": 404," +
                        "\"message\": \"Table " + tableName + " was not found\"" +
                        "}");
            }
        }
    }

//    public JsonNode parseRequestBody(HttpExchange exchange) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        InputStream requestBody = exchange.getRequestBody();
//        Scanner scanner = new Scanner(requestBody).useDelimiter("\\A");
//        String result = scanner.hasNext() ? scanner.next() : "";
//        return objectMapper.readTree(result);
//    }

    public boolean isTableExists(String tableName) {
        Database database = new Database();
        return Arrays.asList(database.getTables()).contains(tableName);
    }

    public boolean isRequestMethodAllowed(String method) {
        return Arrays.asList(requestMethodsAllowed).contains(method);
    }

    public String[] splitRequest(String request, String delimiter) {
        return Arrays.stream(request.split("/")).filter(value -> value != null && value.length() > 0).toArray(String[]::new);
    }

}
