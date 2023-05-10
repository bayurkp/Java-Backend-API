package com.bay.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    public Server(int port) throws IOException {
        this.port = port;

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
        httpServer.setExecutor(Executors.newSingleThreadExecutor());
        httpServer.createContext("/", new Handler());
        httpServer.start();
    }

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

    @Override
    public String toString() {
        return "Server running on port " + this.port + "...";
    }
}
