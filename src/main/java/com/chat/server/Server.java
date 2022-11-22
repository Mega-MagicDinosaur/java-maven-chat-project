package com.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Server {
    private ServerSocket server;
    private final ArrayList<ServerConnection> clients = new ArrayList<>();

    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    private boolean open = false;

    public void read() throws ServerClosedException {
        if (open) {
            try {
                while (open) {
                    System.out.println("clients: " + 
                            clients.stream()
                            .map(ServerConnection::getName)
                            .filter(Objects::nonNull).collect(Collectors.joining(", ")) );
                    // System.out.println("commands: \n@kick [name], \n@mute [name], \n@close");
                    System.out.println("to show commands type '@?'");
                    String[] message = keyboard.readLine().split(" ", 2);
                    switch (message[0]) {
                        case "@mute" -> clients.stream()
                                .filter(client -> client.getName() == message[1])
                                .findAny()
                                .ifPresentOrElse(
                                    client -> client.setMuted(true), 
                                    () -> System.out.println("client not found") );
                        case "@unmute" -> {}
                        case "@kick" -> clients.stream()
                                .filter(client -> client.getName() != null)
                                .filter(client -> client.getName().equals(message[1])).findAny()
                                .ifPresentOrElse(
                                        client -> client.close("you have been kicked out"),
                                        () -> System.out.println("client not found") );
                        case "@exit" -> this.close();
                        default -> System.out.println("unknown command");
                    }

                }
            } catch (IOException e) { e.printStackTrace(); }
        } else { throw new ServerClosedException("server is not open"); }
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
