package com.chat.utils.message;

public enum MessageType {
    CLIENT_SET_NAME,
    CLIENT_SEND_PRIVATE,
    CLIENT_SEND_PUBLIC,
    CLIENT_CLOSE,
    SERVER_SEND_CLIENTS,
    SERVER_SEND_PRIVATE,
    SERVER_SEND_PUBLIC,
    SERVER_APPROVE_NAME,
    SERVER_SEND_ERROR,
    SERVER_CLOSE,
    NULL_MESSAGE
}
