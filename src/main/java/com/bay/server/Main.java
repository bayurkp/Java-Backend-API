package com.bay.server;

import java.io.IOException;

public class Main {
    private static final int port = 8070;
    public static void main(String[] args) throws IOException {
        new Server(port);
    }

}