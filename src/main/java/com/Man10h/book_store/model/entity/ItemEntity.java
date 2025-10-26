package com.Man10h.book_store.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private String status;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity bookEntity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;
}
