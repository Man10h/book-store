package com.Man10h.book_store.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private Long quantity;
    private String status;
    private BookResponse bookResponse;
}
