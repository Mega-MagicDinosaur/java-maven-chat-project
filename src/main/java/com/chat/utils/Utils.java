package com.chat.utils;

import com.chat.utils.message.Message;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class Utils {
    private static final JsonMapper jsonMapper = new JsonMapper();

    public static @Nullable String deserializeJson(Message _message) {
        try { return jsonMapper.writeValueAsString(_message); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static @Nullable Message serializeJson(String _string) {
        try { return jsonMapper.readValue(_string, Message.class); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}