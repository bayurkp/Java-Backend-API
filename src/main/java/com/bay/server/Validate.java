package com.bay.server;

import com.bay.data.Database;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Arrays;

public class Validate {
    public static boolean isTableExists(String tableName) {
        Database database = new Database();
        return Arrays.asList(database.getTables()).contains(tableName);
    }

    public static boolean isRequestMethodAllowed(String requestMethod) {
        return Arrays.asList(new Request().getRequestMethodsAllowed()).contains(requestMethod);
    }

    public static boolean isRequestBodyValid(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()) || "PUT".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestBody().available() != 0) {
                try {
                    new Request().parseRequestBody(exchange);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        }

        return true;
    }
}
