package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "USER API")
public class UserController {

    private final CartService cartService;

    @GetMapping("/carts")
    @Operation(summary = "Get user's cart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserEntity userEntity){
        try{
            return ResponseEntity.ok(cartService.getCart(userEntity));
        }catch (Exception e){
            throw new ErrorException(e.getMessage());
        }
    }

    @PostMapping("/carts/items/{id}")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<?> addItem(@AuthenticationPrincipal UserEntity userEntity,
                                     @RequestBody ItemDTO itemDTO,
                                     @PathVariable("id") Long id){
        try{
            cartService.addItem(userEntity, itemDTO, id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            throw new ErrorException(e.getMessage());
        }
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item in cart")
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
    @Operation(summary = "delete item in carts")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "itemId") Long itemId){
        try{
            cartService.deleteItem(itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

}
