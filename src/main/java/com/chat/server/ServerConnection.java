package com.chat.server;

import com.chat.utils.Utils;
import com.chat.utils.message.ErrorType;
import com.chat.utils.message.Message;
import com.chat.utils.message.MessageType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConnection {
    private final Socket socket;
    private String name;
    private boolean muted;
    private boolean open = false;

    private ArrayList<ServerConnection> clients = new ArrayList<>();

    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    public ServerConnection(Socket _socket) {
        socket = _socket;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Message read() {
        try { return Utils.serializeJson(inputStream.readLine()); }
        catch (SocketException e) { System.out.println("connection with " + name + " was closed."); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    public void write(Message _message) {
        try { outputStream.writeBytes(Utils.deserializeJson(_message) + '\n'); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void open(ArrayList<ServerConnection> _clients) {
        open = true;
        clients = _clients;

        while (open) {
            Message message = this.read();

            if (name==null && message!=null) { this.submitName(message); }
            else {
            }
        }
    }
    public void mess(Message _message) { // ABSOLUTELY CHANGE NAME THIS IS HORRIBLE
        switch ((_message!=null)? _message.getType() : MessageType.NULL_MESSAGE) {
            case CLIENT_CLOSE:
                this.close(null);
                break;
            case CLIENT_SEND_PRIVATE:
                if (this.allowed()) {
                    clients.stream()
                        .filter(client -> client.name != null)
                        .filter(client -> client.name.equals(_message.getReceiver()) )
                        .findAny()
                        .ifPresentOrElse(
                            client -> client.write( new Message(MessageType.SERVER_SEND_PRIVATE, _message.getPayload(), _message.getSender() ) ), 
                            () -> this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.RECEIVER_NOT_FOUND.name() ) ) );
                } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.CLIENT_MUTED.name()) ); }
                break;
            case CLIENT_SEND_PUBLIC:
                if (this.allowed()) {
                    clients.forEach( client -> client.write( new Message(
                        MessageType.SERVER_SEND_PUBLIC,
                        _message.getPayload(),
                        _message.getSender() ) ) );
                } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.CLIENT_MUTED.name()) ); }
                break;
            case CLIENT_SET_NAME:
                this.submitName(_message);
                break; 
        }
    }

    public void close(String _warn) {
        try {
            open = false;
            if (_warn != null) { this.write(new Message(MessageType.SERVER_CLOSE, _warn)); }
            clients.remove(this);
            this.updateClients();
            socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void updateClients() {
        List<String> names = clients.stream()
                .map(ServerConnection::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()); // for arraylist: Collectors.toCollection(ArrayList::new)

        clients.stream()
            .filter(client -> client.name != null)
            .forEach(client -> client.write(new Message(MessageType.SERVER_SEND_CLIENTS, String.join(",", names)) ) );
    }

    public String getName() { return name; }

    public void submitName(@NotNull Message _message) {
        if (_message.getType() == MessageType.CLIENT_SET_NAME) {
            final boolean exists = clients.stream()
                    .map(ServerConnection::getName)
                    .filter(Objects::nonNull)
                    .anyMatch(name -> name.equals(_message.getPayload()) );
            if (exists) {
                this.write(new Message(MessageType.SERVER_SEND_ERROR, ErrorType.NAME_ALREADY_SET.name()));
            } else {
                name = _message.getPayload();
                this.write( new Message(MessageType.SERVER_APPROVE_NAME, name) );
                this.updateClients();
            }
        } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.NAME_NOT_SET.name()) ); }
    }

    public boolean allowed() { return name!=null && !muted; }

    public boolean isMuted() { return muted; }

    public void setMuted(boolean muted) { this.muted = muted; }

}
