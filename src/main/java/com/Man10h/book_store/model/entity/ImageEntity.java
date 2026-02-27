package com.Man10h.book_store.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "image")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity bookEntity;
}
