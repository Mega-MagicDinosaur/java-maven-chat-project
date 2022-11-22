package com.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {
    private ServerSocket server;
    private final ArrayList<ServerConnection> clients = new ArrayList<>();

    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    private boolean open = false;

    public void read() {

    }

    public void open() {
        try {
            open = true;
            server = new ServerSocket(7777);

            while (open) {
                Socket socket = server.accept();
                ServerConnection client = new ServerConnection(socket);
                clients.add(client);
                new Thread(() -> client.open(clients)).start();
            }
        } catch (SocketException e) { System.out.println("server was closed."); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void close() {
        try {
            open = false;
            clients.forEach(client -> client.close("server is closing"));
            server.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

}
