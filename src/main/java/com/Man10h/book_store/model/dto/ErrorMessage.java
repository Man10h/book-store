package com.Man10h.book_store.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {
    private String message;
    private String error;
    private int status;
}
