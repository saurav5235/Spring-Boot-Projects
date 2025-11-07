package com.app.chatapp.model;

import lombok.Data;

@Data
public class ChatMessage {
    private Long id;
    private String sender;
    private String content;
}