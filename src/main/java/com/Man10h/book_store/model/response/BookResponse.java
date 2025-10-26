package com.Man10h.book_store.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String type;
    private String description;
    private Double price;
    private List<String> imagesStringUrl;

    public BookResponse(Long id, String title, String author, String type) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.type = type;
    }
}
