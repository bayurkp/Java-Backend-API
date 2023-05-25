package com.bay.server;

import com.bay.data.Database;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
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
            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/json"));
            int statusCode;

            String requestMethod = exchange.getRequestMethod();

            String requestQuery = exchange.getRequestURI().getQuery();
            String condition = new Request().parseRequestQuery(requestQuery);
            System.out.println(condition);

            String[] requestPath = new Request().splitString(exchange.getRequestURI().getPath(), "/");
            String tableName = null;
            try {
                tableName = requestPath[0];
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
                e.printStackTrace();
            }


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

            new Response(exchange).send(statusCode = 200, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"OK\"," +
                    "\"data\": " +
                    new Database().select(tableName, condition) +
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

    public String[] splitString(String text, String delimiter) {
        return Arrays.stream(text.split(delimiter)).filter(value -> value != null && value.length() > 0).toArray(String[]::new);
    }

    public String parseRequestQuery(String query) {
        String[] queryParsed = this.splitString(query, "&");
        String field = "";
        String condition = "";
        String value = "";
        for (String param : queryParsed) {
            System.out.println(param);
            String paramKey = this.splitString(param, "=")[0];
            String paramValue = this.splitString(param, "=")[1];

            if (paramKey.equals("f")) {
                field = paramValue;
            }

            if (paramKey.equals("c")) {
                if (paramValue.equals("greaterEqual")) condition = ">=";
                else if (paramValue.equals("greater")) condition = ">";
                else if (paramValue.equals("lessEqual")) condition = "<=";
                else if (paramValue.equals("less")) condition = "<";
                else if (paramValue.equals("equal")) condition = "=";
                else if (paramValue.equals("notEqual")) condition = "<>";
            }

            if (paramKey.equals("v")) {
                value = paramValue;
            }
        }
        return field + condition + value;
    }

    public String[] getRequestMethodsAllowed() {
        return requestMethodsAllowed;
    }
}
