package com.bay.server;

import java.io.IOException;

public class Main {
    private static final String nim = "2205551070";
    private static final int port = Integer.parseInt("8" + nim.substring(nim.length() - 3));
    public static void main(String[] args) throws IOException {
        new Server(port);
    }

}