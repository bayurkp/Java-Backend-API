package com.bay.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Request {
    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equals("GET") ||
                    exchange.getRequestMethod().equals("POST") ||
                    exchange.getRequestMethod().equals("PUT") ||
                    exchange.getRequestMethod().equals("DELETE")) {
                new Response(exchange, 200, "Hello, World!");
//                new Response(exchange, 200, "{\"statusCode\": 200, " + "\"message\": \"Hello, " + parseRequestBody(exchange).get("name").asText() + "!\"}");
            } else {
                new Response(exchange, 405, "{\"statusCode\": 405, \"message\": \"Method not allowed\"}");
            }
        }
    }

    public static JsonNode parseRequestBody(HttpExchange exchange) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream requestBody = exchange.getRequestBody();
        Scanner scanner = new Scanner(requestBody).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        return objectMapper.readTree(result);
    }
}
