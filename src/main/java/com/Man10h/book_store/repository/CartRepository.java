package com.Man10h.book_store.repository;

import com.Man10h.book_store.model.entity.CartEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @EntityGraph(value = "cart-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<CartEntity> findById(Long id);
}
