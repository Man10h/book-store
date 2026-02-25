package com.Man10h.book_store.service;

import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.CartResponse;

public interface CartService {
    public CartResponse getCart(UserEntity userEntity);
    public void addItem(UserEntity userEntity, ItemDTO itemDTO, Long id);
    public void updateItem(ItemDTO itemDTO, Long itemId);
    public void deleteItem(Long itemId);
}
