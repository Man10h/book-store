package com.Man10h.book_store.services.unit_test;

import com.Man10h.book_store.exception.business.BookNotFoundException;
import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.entity.ImageEntity;
import com.Man10h.book_store.repository.BookRepository;
import com.Man10h.book_store.repository.ImageRepository;
import com.Man10h.book_store.service.CloudinaryService;
import com.Man10h.book_store.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Spy
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void should_add_images(){
        //Arrange input
        BookEntity bookEntity = BookEntity.builder()
                .imageEntityList(new ArrayList<>())
                .build();
        MultipartFile multipartFile = mock(MultipartFile.class);
        List<MultipartFile> images = new ArrayList<>();
        images.add(multipartFile);

        //Arrange method
        Map<String, Object> result = new HashMap<>();
        result.put("url", "url");
        when(cloudinaryService.upload(any())).thenReturn(result);
        when(imageRepository.save(any())).thenReturn(mock(ImageEntity.class));


        //Act
        bookService.addImage(bookEntity, images);


        //Assert
        assertEquals("url", bookEntity.getImageEntityList().getFirst().getUrl());
        verify(imageRepository).save(any());
        verify(cloudinaryService).upload(any());
    }

    @Test
    public void should_not_add_images_when_images_is_null(){
        BookEntity bookEntity = BookEntity.builder()
                .imageEntityList(new ArrayList<>())
                .build();

        bookService.addImage(bookEntity, null);

        verify(cloudinaryService, never()).upload(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void should_not_add_images_when_images_size_0(){
        BookEntity bookEntity = BookEntity.builder()
                .imageEntityList(new ArrayList<>())
                .build();
        List<MultipartFile> images = new ArrayList<>();

        bookService.addImage(bookEntity, images);

        verify(cloudinaryService, never()).upload(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void should_add_book(){
        BookDTO bookDTO = BookDTO.builder()
                .title("title")
                .build();
        MultipartFile multipartFile = mock(MultipartFile.class);
        List<MultipartFile> images = new ArrayList<>();
        images.add(multipartFile);

        Map<String, Object> result = new HashMap<>();
        result.put("url", "url");
        when(cloudinaryService.upload(any())).thenReturn(result);

        bookService.addBook(bookDTO, images);

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());
        verify(bookService, times(1)).addImage(any(), any());

        assertEquals("title", captor.getValue().getTitle());
        assertEquals("url", captor.getValue().getImageEntityList().getFirst().getUrl());
    }

    @Test
    public void should_add_book_when_images_is_null(){
        BookDTO bookDTO = BookDTO.builder()
                .title("title")
                .build();

        bookService.addBook(bookDTO, null);

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository, times(1)).save(captor.capture());
        verify(bookService, times(1)).addImage(any(), any());
        assertEquals("title", captor.getValue().getTitle());
    }

    @Test
    public void should_update_book(){
        Long id = 1L;
        BookDTO bookDTO = BookDTO.builder()
                .title("new_title")
                .build();
        BookEntity bookEntity = BookEntity.builder()
                .id(id)
                .title("old_title")
                .build();

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);
        doNothing().when(bookService).addImage(any(), any());
        bookService.updateBook(id, bookDTO, null);

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository, times(1)).save(captor.capture());
        verify(bookRepository, times(1)).findById(any());

        assertEquals("new_title", captor.getValue().getTitle());
    }

    @Test
    public void should_not_update_when_not_found_book(){
        Long id = 1L;
        BookDTO bookDTO = BookDTO.builder()
                .title("new_title")
                .build();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(id, bookDTO, null));
        verify(bookRepository, never()).save(any());
    }


    @Test
    public void should_delete_book(){
        Long id = 1L;
        BookEntity bookEntity = BookEntity.builder()
                .id(id)
                .build();

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        doNothing().when(bookRepository).delete(any());

        bookService.deleteBook(id);

        verify(bookRepository, times(1)).findById(any());
        verify(bookRepository, times(1)).delete(any());
    }

    @Test
    public void should_not_delete_book_when_not_found_book(){
        Long id = 1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(id));
    }
}
