package com.chat.server;

public class ServerClosedException extends Exception {
    public ServerClosedException(String _warn) { 
        super(_warn); 
    }
}
