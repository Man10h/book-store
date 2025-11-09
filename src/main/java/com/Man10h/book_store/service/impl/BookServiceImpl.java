package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.BookException;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;

    public int randomTtl(){
        return 2 + new Random().nextInt(3);
    }

    public void addImage(BookEntity bookEntity, BookDTO bookDTO) {
        if(bookDTO.getImageMultipartFiles() != null) {
            for(MultipartFile file : bookDTO.getImageMultipartFiles()) {
                Map<String, Object> result = cloudinaryService.upload(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .url(result.get("url").toString())
                        .bookEntity(bookEntity)
                        .build();
                imageRepository.save(imageEntity);
            }
        }
    }

    @Override
    @Cacheable(value = "books", key = "#text + '_' + #type")
    public Page<BookResponse> findByTitleAndAuthorAndType(String text, String type, Pageable pageable) {
        return bookRepository.findByTitleAndAuthorAndType(text, type, pageable).map(bookEntity -> {
            return BookResponse.builder()
                    .id(bookEntity.getId())
                    .title(bookEntity.getTitle())
                    .author(bookEntity.getAuthor())
                    .type(bookEntity.getType())
                    .imagesStringUrl(bookEntity.getImageEntityList().stream().map(ImageEntity::getUrl).collect(Collectors.toList()))
                    .build();
        });
    }


    @Override
    @Cacheable(value = "books", key = "#id")
    public BookResponse findById(Long id) {
        Optional<BookEntity> optional = bookRepository.findById(id);
        if(optional.isEmpty()){
            throw new BookException("Book not found");
        }
        BookEntity bookEntity = optional.get();
        return BookResponse.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .type(bookEntity.getType())
                .price(bookEntity.getPrice())
                .description(bookEntity.getDescription())
                .imagesStringUrl(bookEntity.getImageEntityList().stream().map(ImageEntity::getUrl).collect(Collectors.toList()))
                .build();

    }

    @Override
    public void addBook(BookDTO bookDTO) {
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
        addImage(bookEntity, bookDTO);
    }

    @Override
    @CachePut(value = "books", key = "#id")
    public void updateBook(BookDTO bookDTO, Long id) {
        String key = "books:" + id;
        Optional<BookEntity> optional = bookRepository.findById(id);
        if(optional.isEmpty()){
            throw new BookException("Book not found");
        }
        BookEntity bookEntity = optional.get();
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
        addImage(bookEntity, bookDTO);
    }

    @Override
    @CacheEvict(value = "books", key = "#id")
    public void deleteBook(Long id) {
        try{
            bookRepository.deleteById(id);
        }catch (Exception e){
            throw new BookException("Book not found");
        }
    }
}
