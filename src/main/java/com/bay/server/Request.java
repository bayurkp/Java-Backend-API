package com.bay.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        public void handle(HttpExchange exchange) {
            try {
                Response response = new Response(exchange);

                exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
                int statusCode;

                String requestMethod = exchange.getRequestMethod();
                String requestQuery = exchange.getRequestURI().getQuery();
                String[] requestPath = Parser.splitString(exchange.getRequestURI().getPath(), "/");
                String requestBody = Parser.parseInputStream(exchange.getRequestBody());

                String tableMaster = null;
                String id = null;
                String tableDetail = null;

                try {
                    tableMaster = requestPath[0];
                    if (requestPath.length > 1) id = requestPath[1];
                    if (requestPath.length > 2) tableDetail = requestPath[2];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!Validate.isRequestMethodAllowed(requestMethod)) {
                    response.send(statusCode = 405, "{" +
                            "\"status\": " + statusCode + "," +
                            "\"message\": \"Request method " + requestMethod + " not allowed\"" +
                            "}");
                    return;
                }

                if (!Validate.isTableNameValid(tableMaster)) {
                    response.send(statusCode = 404, "{" +
                            "\"status\": " + statusCode + "," +
                            "\"message\": \"Table " + tableMaster + " was not found\"" +
                            "}");
                    return;
                }

                if (id != null && !Validate.isIdValid(id)) {
                    response.send(statusCode = 404, "{" +
                            "\"status\": " + statusCode + "," +
                            "\"message\": \"ID " + id + " was not found\"" +
                            "}");
                    return;
                }

                if (tableDetail != null && !Validate.isTableNameValid(tableDetail)) {
                    response.send(statusCode = 404, "{" +
                            "\"status\": " + statusCode + "," +
                            "\"message\": \"Table " + tableDetail + " was not found\"" +
                            "}");
                    return;
                }

                String condition = Parser.parseRequestQuery(requestQuery);
                if (Validate.isRequestMethodAllowed(requestMethod) && !"POST".equals(requestMethod) && requestQuery != null) {
                    if (condition == null) {
                        response.send(statusCode = 400, "{" +
                                "\"status\": " + statusCode + "," +
                                "\"message\": \"Query " + requestQuery + " is not valid\"" +
                                "}");
                    }
                    return;
                }

                if (!Validate.isRequestBodyValid(requestBody)) {
                    response.send(statusCode = 400, "{" +
                            "\"status\": " + statusCode + "," +
                            "\"message\": \"Please input request body as JSON for insert and update data\"" +
                            "}");
                    return;
                }

                JsonNode jsonNode = Parser.parseJson(requestBody);

                switch (requestMethod) {
                    case "GET":
                        // Do this when the user only routes to the table name without adding a query in the URL
                        if (requestPath.length == 1 && requestQuery != null) {
                            response.handleGet(tableMaster, condition);
                        // Do when URL just route to the table without query
                        } else if (requestPath.length == 1) {
                            response.handleGet(tableMaster, null);
                        } else if (requestPath.length == 2 && requestQuery == null) {
                            assert id != null;
                            response.handleGet(tableMaster, Integer.parseInt(id), null);
                        } else if (requestPath.length == 3 && requestQuery == null) {
                            assert id != null;
                            response.handleGet(tableMaster, Integer.parseInt(id), tableDetail);
                        }
                        return;
                    case "POST":
                        response.handlePost(tableMaster, jsonNode);
                        return;
                    case "PUT":
                        if (requestPath.length == 2) {
                            assert id != null;
                            response.handlePut(tableMaster, Integer.parseInt(id), jsonNode);
                        }
                        return;
                    case "DELETE":
                        if (requestPath.length == 2) {
                            assert id != null;
                            response.handleDelete(tableMaster, Integer.parseInt(id));
                        }
                        return;
                    default:
                        response.send(statusCode = 400, "{" +
                                "\"status\": " + statusCode + "," +
                                "\"message\": \"Please check your way\"" +
                                "}");
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
