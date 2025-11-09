package com.Man10h.book_store.service;

import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.response.CartResponse;

public interface CartService {
    public CartResponse findById(Long id);
    public void addItem(ItemDTO itemDTO, Long cartId);
    public void updateItem(ItemDTO itemDTO, Long itemId);
    public void deleteItem(Long itemId);
}
