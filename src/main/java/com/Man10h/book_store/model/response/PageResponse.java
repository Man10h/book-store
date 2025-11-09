package com.Man10h.book_store.model.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse implements Serializable {
    private int size;
    private int page;
    private long totalElements;
    private int totalPages;
    private List<Object> contents;
}
