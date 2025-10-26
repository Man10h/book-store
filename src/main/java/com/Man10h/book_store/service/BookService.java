package com.Man10h.book_store.service;

import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface BookService {
    public Page<BookResponse> findByTitleAndAuthorAndType(String text, String type, Pageable pageable);
    public BookResponse findById(Long id);
    public void addBook(BookDTO bookDTO);
    public void updateBook(BookDTO bookDTO, Long id);
    public void deleteBook(Long id);
}
