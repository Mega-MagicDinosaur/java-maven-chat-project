package com.chat.utils.message;

public class Message {
    private MessageType type;
    private String payload;
    private String sender;
    private String receiver;

    public Message() {}
    public Message(MessageType _type) {
        type = _type;
    }
    public Message(MessageType _type, String _payload) {
        type = _type;
        payload = _payload;
    }
    public Message(MessageType _type, String _payload, String _sender) {
        type = _type;
        payload = _payload;
        sender = _sender;
    }
    public Message(MessageType _type, String _payload, String _sender, String _receiver) {
        type = _type;
        payload = _payload;
        sender = _sender;
        receiver = _receiver;
    }

    public MessageType getType() { return type; }
    public String getPayload() { return payload; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }

    public void setType(MessageType type) { this.type = type; }
    public void setPayload(String payload) { this.payload = payload; }
    public void setSender(String sender) { this.sender = sender; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
}
