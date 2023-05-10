package com.bay.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Request {
    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equals("GET") ||
                    exchange.getRequestMethod().equals("POST") ||
                    exchange.getRequestMethod().equals("PUT") ||
                    exchange.getRequestMethod().equals("DELETE")) {
                new Response(exchange, 200, "{\"statusCode\": 200, \"message\": \"Hello "  + exchange.getRequestMethod() + "\"}");
            } else {
                new Response(exchange, 405, "{\"statusCode\": 405, \"message\": \"Method not allowed\"}");
            }
        }
    }
}
