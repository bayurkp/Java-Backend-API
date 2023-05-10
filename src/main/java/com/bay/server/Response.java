package com.bay.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class Response {
    public Response(HttpExchange exchange) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        String response = "Hello, ";
        if ("GET".equals(exchange.getRequestMethod())) {
            response += exchange.getRequestMethod();
            exchange.sendResponseHeaders(200, response.length());
        } else if ("POST".equals(exchange.getRequestMethod())) {
            response += exchange.getRequestMethod();
            exchange.sendResponseHeaders(200, response.length());
        } else if ("PUT".equals(exchange.getRequestMethod())) {
            response += exchange.getRequestMethod();
            exchange.sendResponseHeaders(200, response.length());
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            response += exchange.getRequestMethod();
            exchange.sendResponseHeaders(200, response.length());
        } else {
            response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
        }
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
