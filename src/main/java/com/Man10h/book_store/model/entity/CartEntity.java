package com.Man10h.book_store.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "cart-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "itemEntityList", subgraph = "item-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "item-subgraph",
                        attributeNodes = {
                          @NamedAttributeNode(value = "bookEntity")
                        }
                )
        }
)
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cartEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<ItemEntity> itemEntityList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
