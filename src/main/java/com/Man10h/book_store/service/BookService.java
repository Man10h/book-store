package com.Man10h.book_store.service;

import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.response.BookResponse;
import com.Man10h.book_store.model.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    public Page<BookResponse> findByTitleAndAuthorAndType(String text, String type, Pageable pageable);
    public BookResponse findById(Long id);
    public void addBook(BookDTO bookDTO, List<MultipartFile> images);
    public void updateBook(Long id, BookDTO bookDTO, List<MultipartFile> images);
    public void deleteBook(Long id);
}
