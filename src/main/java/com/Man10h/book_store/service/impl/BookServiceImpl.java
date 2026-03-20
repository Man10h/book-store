package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.business.BookNotFoundException;
import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.entity.ImageEntity;
import com.Man10h.book_store.model.response.BookResponse;
import com.Man10h.book_store.model.response.PageResponse;
import com.Man10h.book_store.repository.BookRepository;
import com.Man10h.book_store.repository.ImageRepository;
import com.Man10h.book_store.service.BookService;
import com.Man10h.book_store.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;

    private BookResponse toBookResponse(BookEntity bookEntity) {
        return BookResponse.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .type(bookEntity.getType())
                .price(bookEntity.getPrice())
                .description(bookEntity.getDescription())
                .imagesStringUrl(bookEntity.getImageEntityList().stream()
                        .map(ImageEntity::getUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    public void addImage(BookEntity bookEntity, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                Map<String, Object> result = cloudinaryService.upload(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .url(result.get("url").toString())
                        .bookEntity(bookEntity)
                        .build();
                bookEntity.getImageEntityList().add(imageEntity);
                imageRepository.save(imageEntity);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#text + '_' + #type + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BookResponse> findByTitleAndAuthorAndType(String text, String type, Pageable pageable) {
        return bookRepository.findByTitleAndAuthorAndType(text, type, pageable).map(this::toBookResponse);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "book", key = "#id")
    public BookResponse findById(Long id) {
        BookEntity bookEntity = bookRepository.findDetailById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        return toBookResponse(bookEntity);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void addBook(BookDTO bookDTO, List<MultipartFile> images) {
        log.info("Adding new book {}", bookDTO.getTitle());
        BookEntity bookEntity = BookEntity.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .type(bookDTO.getType())
                .price(bookDTO.getPrice())
                .description(bookDTO.getDescription())
                .imageEntityList(new ArrayList<>())
                .itemEntityList(new ArrayList<>())
                .build();
        bookRepository.save(bookEntity);
        addImage(bookEntity, images);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public void updateBook(Long id, BookDTO bookDTO, List<MultipartFile> images) {
        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        if(bookDTO.getTitle() != null){
            bookEntity.setTitle(bookDTO.getTitle());
        }
        if(bookDTO.getAuthor()!= null){
            bookEntity.setAuthor(bookDTO.getAuthor());
        }
        if(bookDTO.getType()!= null){
            bookEntity.setType(bookDTO.getType());
        }
        if(bookDTO.getDescription()!= null){
            bookEntity.setDescription(bookDTO.getDescription());
        }
        if(bookDTO.getPrice() != null){
            bookEntity.setPrice(bookDTO.getPrice());
        }
        bookRepository.save(bookEntity);
        addImage(bookEntity, images);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public void deleteBook(Long id) {
        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        bookRepository.delete(bookEntity);
    }
}
