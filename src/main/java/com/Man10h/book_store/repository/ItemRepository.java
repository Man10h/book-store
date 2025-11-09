package com.Man10h.book_store.repository;

import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = """
    insert into item(quantity, status, book_id, cart_id)
    values (:quantity, :status, :bookId, :cartId)
""", nativeQuery = true)
    void insert(@Param("quantity") Long quantity,
                @Param("status") String status,
                @Param("bookId") Long bookId,
                @Param("cartId") Long cartId);



    @Modifying
    @Transactional
    @Query(value = """
    update ItemEntity i set i.quantity = :quantity
    where i.id = :id AND :quantity is not  null
""")
    void updateQuantity(@Param("id") Long id, @Param("quantity") Long quantity);

    @Modifying
    @Transactional
    @Query(value = """
    update ItemEntity i set i.status = :status
    where i.id = :id AND :status is not  null
""")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
