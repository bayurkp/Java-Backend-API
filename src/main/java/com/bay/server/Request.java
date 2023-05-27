package com.bay.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.PasswordAuthentication;
import java.util.*;

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
            Response response = new Response(exchange);

            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
            int statusCode;

            String requestMethod = exchange.getRequestMethod();
            String requestQuery = exchange.getRequestURI().getQuery();
            String[] requestPath = Parser.splitString(exchange.getRequestURI().getPath(), "/");
            String requestBody = Parser.parseInputStream(exchange.getRequestBody());

            String tableName = null;
            try {
                tableName = requestPath[0];
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            if (!Validate.isRequestMethodAllowed(requestMethod)) {
                response.send(statusCode = 405, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Request method " + requestMethod + " not allowed\"" +
                        "}");
            }

            if (!Validate.isTableNameValid(tableName)) {
                response.send(statusCode = 404, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Table " + tableName + " was not found\"" +
                        "}");
            }

            String condition = null;
            if (Validate.isRequestMethodAllowed(requestMethod) && !"POST".equals(requestMethod) && !requestQuery.isEmpty()) {
                condition = Parser.parseRequestQuery(requestQuery);
                if (condition == null) response.send(statusCode = 400, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Query " + requestQuery + " is not valid\"" +
                        "}");
            }

            if (!Validate.isRequestBodyValid(requestBody)) {
                response.send(statusCode = 400, "{" +
                        "\"status\": " + statusCode + "," +
                        "\"message\": \"Please input request body as JSON for insert and update data\"" +
                        "}");
            }

            JsonNode jsonNode = Parser.parseJson(requestBody);
            if (requestMethod.equals("GET")) response.handleGet(tableName, condition);
            if (requestMethod.equals("POST")) response.handlePost(tableName, jsonNode);
        }
    }

    public String[] getRequestMethodsAllowed() {
        return requestMethodsAllowed;
    }
}
