package com.bay.server;

import com.bay.data.Database;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Arrays;

public class Validate {
    public static boolean isRequestMethodAllowed(String requestMethod) {
        return Arrays.asList(new Request().getRequestMethodsAllowed()).contains(requestMethod);
    }

    public static boolean isTableNameValid(String tableName) {
        Database database = new Database();
        return Arrays.asList(database.getTables()).contains(tableName);
    }

    public static boolean isRequestBodyValid(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()) ||
                "PUT".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestBody().available() != 0) {
                try {
                    Parser.parseJson(exchange.getRequestBody());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            } else
                return false;
        }

        return true;
    }
}
