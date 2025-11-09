package com.Man10h.book_store.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String message;
    private String sender;
    private String recipient;
}

