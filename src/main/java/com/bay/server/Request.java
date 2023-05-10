package com.bay.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class Request {
    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();
            String response = "Hello, world";
            exchange.sendResponseHeaders(200, response.length());
            outputStream.write(response.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}
