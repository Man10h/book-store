package com.Man10h.book_store.model.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private List<ItemResponse> itemResponseList;
}
