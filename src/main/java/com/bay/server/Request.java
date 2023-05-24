package com.bay.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

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
            int statusCode;
            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
            String[] requestPath = new Request().splitRequest(exchange.getRequestURI().getPath(), "/");
            String tableName = null;
            try {
                tableName = requestPath[0];
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
            }
            String requestMethod = exchange.getRequestMethod();

            if (!Validate.isRequestMethodAllowed(requestMethod)) {
                new Response(exchange).send(statusCode = 405, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Method not allowed\"" +
                        "}");
            }

            if (!Validate.isTableExists(tableName)) {
                new Response(exchange).send(statusCode = 404, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Table " + tableName + " was not found\"" +
                        "}");
            }

            if (!Validate.isRequestBodyValid(exchange)) {
                new Response(exchange).send(statusCode = 400, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message: Please input request body as JSON for insert and update data\"" +
                        "}");
            }

            new Response(exchange).send(statusCode = 404, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"Still Working\"" +
                    "}");
        }
    }

    public JsonNode parseRequestBody(HttpExchange exchange) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream requestBody = exchange.getRequestBody();
        Scanner scanner = new Scanner(requestBody).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        return objectMapper.readTree(result);
    }

    public String[] splitRequest(String request, String delimiter) {
        return Arrays.stream(request.split(delimiter)).filter(value -> value != null && value.length() > 0).toArray(String[]::new);
    }

    public String[] getRequestMethodsAllowed() {
        return requestMethodsAllowed;
    }
}
