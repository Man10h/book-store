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
    // no page + fetch only page or only fetch if want to fetch using entity graph
    @Query("""
    select distinct b from BookEntity b 
    where (
             :text is null\s
             or b.title like concat('%', :text, '%')
             or b.author like concat('%', :text, '%')
      )
    or (:type is null or b.type = :type)
                                          
""")
    Page<BookEntity> findByTitleAndAuthorAndType(@Param("text") String text,
                                          @Param("type") String type,
                                          Pageable pageable);



    // query projection faster than map to entity
    @Query("""
    select distinct b from BookEntity b 
    left join fetch b.imageEntityList
    where b.id = :id
""")
    Optional<BookEntity> findDetailById(@Param("id") long id);
}
