package com.bay.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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
            try {
                Response response = new Response(exchange);

                exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
                int statusCode;

                String requestMethod = exchange.getRequestMethod();
                String requestQuery = exchange.getRequestURI().getQuery();
                String[] requestPath = Parser.splitString(exchange.getRequestURI().getPath(), "/");
                String requestBody = Parser.parseInputStream(exchange.getRequestBody());

                String tableName = null;
                int id = 0;
                String tableName2 = null;
                try {
                    tableName = requestPath[0];
                    if (requestPath.length == 2) id = Integer.parseInt(requestPath[1]);
                    if (requestPath.length == 3) tableName2 = requestPath[2];
                } catch (Exception e) {
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
                if (Validate.isRequestMethodAllowed(requestMethod) && !"POST".equals(requestMethod) && requestQuery != null) {
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
                if (requestMethod.equals("PUT")) response.handlePut(tableName, id, jsonNode);
                if (requestMethod.equals("DELETE")) response.handleDelete(tableName, id);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }


        }
    }

    public String[] getRequestMethodsAllowed() {
        return requestMethodsAllowed;
    }
}
