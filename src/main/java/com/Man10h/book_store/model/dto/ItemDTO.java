package com.Man10h.book_store.model.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long quantity;
    private String status;
}
