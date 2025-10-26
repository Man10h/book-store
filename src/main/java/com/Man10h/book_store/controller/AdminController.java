package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.exception.BookException;
import com.Man10h.book_store.exception.exception.UserException;
import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.service.BookService;
import com.Man10h.book_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final BookService bookService;

    //Module: Management user
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(name = "page") int page,
                                      @RequestParam(name = "size") int size,
                                      @RequestParam(name = "username") String username) {
        if(username == null) {
            return ResponseEntity.ok(userService.getUsers(PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(userService.getUsersByUsername(username, PageRequest.of(page, size)));
    }


    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUserRole(@PathVariable(name = "userId") Long userId){
        try{
            userService.updateUserRole(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
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
    public ResponseEntity<?> getBooks(@RequestParam(name = "text") String text,
                                      @RequestParam(name = "type") String type,
                                      @RequestParam(name = "page") int page,
                                      @RequestParam(name = "size") int size){
        return ResponseEntity.ok(bookService.findByTitleAndAuthorAndType(text, type, PageRequest.of(page, size)));
    }


    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBook(@ModelAttribute BookDTO bookDTO){
        try{
            bookService.addBook(bookDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BookException(e.getMessage());
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@ModelAttribute BookDTO bookDTO, @PathVariable(name = "id") Long id){
        try{
            bookService.updateBook(bookDTO, id);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            throw new BookException(e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable(name = "id") Long id){
        try{
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            throw new BookException(e.getMessage());
        }
    }
}
