package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {
    private static final int PORT = 9999;

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server is running on the port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client: " + client.getInetAddress());
                pool.execute(new ClientHandler(client));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
