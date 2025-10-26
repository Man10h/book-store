package com.Man10h.book_store.repository;

import com.Man10h.book_store.model.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    @Query("""
    select distinct b from BookEntity b 
    left join fetch b.imageEntityList
    where (:text is null or b.title like %:text%) 
    or (:text is null or b.author like %:text%)
    or (:type is null or b.type = :type) 
""")
    Page<BookEntity> findByTitleAndAuthorAndType(@Param("text") String text,
                                          @Param("type") String type,
                                          Pageable pageable);



    @Query("""
    select distinct b from BookEntity b 
    left join fetch b.imageEntityList
    where b.id = :id
""")
    Optional<BookEntity> findById(@Param("id") long id);
}
