package com.bay.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
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

                OutputStream outputStream = exchange.getResponseBody();
                String requestMethod = exchange.getRequestMethod();
                String requestQuery = exchange.getRequestURI().getQuery();
                String[] requestPath = Parser.splitString(exchange.getRequestURI().getPath(), "/");
                String requestBody = Parser.parseInputStream(exchange.getRequestBody());

                String tableName = null;
                String id = null;
                String tableName2 = null;

                try {
                    tableName = requestPath[0];
                    if (requestPath.length > 1) id = requestPath[1];
                    if (requestPath.length > 2) tableName2 = requestPath[2];
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
                switch (requestMethod) {
                    case "GET":
                        // Do this when the user only routes to the table name without adding a query in the URL
                        if (requestPath.length == 1 && requestQuery != null) {
                            response.handleGet(tableName, condition);

                            // Do when URL just route to the table without query
                        } else if (requestPath.length == 1) {
                            response.handleGet(tableName, null);

                        } else if (requestPath.length == 2 && requestQuery == null) {
                            response.handleGet(tableName, Integer.parseInt(id), null);
                        } else if (requestPath.length == 3 && requestQuery == null) {
                            response.handleGet(tableName, Integer.parseInt(id), tableName2);
                        }
                        break;
                    case "POST":
                        response.handlePost(tableName, jsonNode);
                        break;
                    case "PUT":
                        if (requestPath.length == 2) {
                            response.handlePut(tableName, Integer.parseInt(id), jsonNode);
                        }
                        break;
                    case "DELETE":
                        if (requestPath.length == 2) {
                            response.handleDelete(tableName, Integer.parseInt(id));
                        }
                        break;
                }
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
