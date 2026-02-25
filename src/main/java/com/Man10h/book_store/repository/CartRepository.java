package com.Man10h.book_store.repository;

import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @EntityGraph(value = "cart-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<CartEntity> findById(Long id);


    @EntityGraph(value = "cart-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<CartEntity> findByUserEntity(UserEntity userEntity);
}
