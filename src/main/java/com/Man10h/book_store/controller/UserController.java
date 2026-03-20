package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.ErrorException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.service.CartService;
import com.Man10h.book_store.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "USER API")
public class UserController {

    private final CartService cartService;
    private final PaymentService paymentService;

    @GetMapping("/carts")
    @Operation(summary = "Get user's cart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserEntity userEntity){
        return ResponseEntity.ok(cartService.getCart(userEntity));
    }

    @PostMapping("/carts/items/{id}")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<?> addItem(@AuthenticationPrincipal UserEntity userEntity,
                                     @Valid @RequestBody ItemDTO itemDTO,
                                     @PathVariable("id") Long id){
        cartService.addItem(userEntity, itemDTO, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item in cart")
    public ResponseEntity<?> updateItem(@PathVariable(name = "itemId") Long itemId,
                                        @Valid @RequestBody ItemDTO itemDTO,
                                        @AuthenticationPrincipal UserEntity userEntity){
        cartService.updateItem(itemDTO, itemId, userEntity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "delete item in carts")
    public ResponseEntity<?> deleteItem(@PathVariable(name = "itemId") Long itemId,
                                        @AuthenticationPrincipal UserEntity userEntity){
        cartService.deleteItem(itemId, userEntity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkout")
    @Operation(summary = "checkout order")
    public ResponseEntity<String> checkout(@AuthenticationPrincipal UserEntity userEntity){
        return ResponseEntity.ok(paymentService.checkout(userEntity));
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params) {
        paymentService.callback(params);
        return ResponseEntity.ok().build();
    }
}
