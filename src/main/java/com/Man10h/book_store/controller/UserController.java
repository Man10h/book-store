package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final CartService cartService;

    @GetMapping("/carts/{id}")
    public ResponseEntity<?> getCart(@PathVariable(name = "id") Long id){
        try{
            return ResponseEntity.ok(cartService.findById(id));
        }catch (Exception e){
            throw new ErrorException(e.getMessage());
        }
    }

    @PostMapping("/carts/{id}")
    public ResponseEntity<?> addItem(@PathVariable(name = "id") Long id, @RequestBody ItemDTO itemDTO){
        try{
            cartService.addItem(itemDTO, id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            throw new ErrorException(e.getMessage());
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable(name = "itemId") Long itemId,
                                        @RequestBody ItemDTO itemDTO){
        try{
            cartService.updateItem(itemDTO, itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "itemId") Long itemId){
        try{
            cartService.deleteItem(itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

}
