package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.exception.BookException;
import com.Man10h.book_store.exception.exception.UserException;
import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.service.BookService;
import com.Man10h.book_store.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "ADMIN API")
public class AdminController {
    private final UserService userService;
    private final BookService bookService;

    //Module: Management user
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getUsers(@RequestParam(name = "page") int page,
                                      @RequestParam(name = "size") int size,
                                      @RequestParam(name = "username", required = false) String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.ok(userService.getUsers(PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(userService.getUsersByUsername(username, PageRequest.of(page, size)));
    }


    @PutMapping("/users/{userId}")
    @Operation(summary = "Update user's role")
    public ResponseEntity<?> updateUserRole(@PathVariable(name = "userId") Long userId){
        try{
            userService.updateUserRole(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") Long userId){
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    //Module: Management Book
    @GetMapping("/books")
    @Operation(summary = "Get all books")
    public ResponseEntity<?> getBooks(@RequestParam(name = "text") String text,
                                      @RequestParam(name = "type") String type,
                                      @RequestParam(name = "page") int page,
                                      @RequestParam(name = "size") int size){
        return ResponseEntity.ok(bookService.findByTitleAndAuthorAndType(text, type, PageRequest.of(page, size)));
    }


    @GetMapping("/books/{id}")
    @Operation(summary = "Get book by Id")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping("/books")
    @Operation(summary = "Add book")
    public ResponseEntity<?> addBook(@ModelAttribute BookDTO bookDTO,
                                     @RequestPart(name = "images") List<MultipartFile> images){
        try{
            bookService.addBook(bookDTO, images);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BookException(e.getMessage());
        }
    }

    @PutMapping("/books/{id}")
    @Operation(summary = "Update book")
    public ResponseEntity<?> updateBook(@ModelAttribute BookDTO bookDTO,
                                        @PathVariable(name = "id") Long id,
                                        @RequestPart(name = "images") List<MultipartFile> images){
        try{
            bookService.updateBook(id, bookDTO, images);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            throw new BookException(e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    @Operation(summary = "Delete book")
    public ResponseEntity<?> deleteBook(@PathVariable(name = "id") Long id){
        try{
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            throw new BookException(e.getMessage());
        }
    }
}
