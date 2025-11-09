package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.ImageEntity;
import com.Man10h.book_store.model.entity.ItemEntity;
import com.Man10h.book_store.model.response.BookResponse;
import com.Man10h.book_store.model.response.CartResponse;
import com.Man10h.book_store.model.response.ItemResponse;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.ItemRepository;
import com.Man10h.book_store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Override
    @Cacheable(value = "carts", key = "#id")
    public CartResponse findById(Long id) {
        Optional<CartEntity> optional = cartRepository.findById(id);
        if(optional.isEmpty()){
            throw new ErrorException("Cart not found");
        }
        CartEntity cartEntity = optional.get();
        CartResponse cartResponse = CartResponse.builder()
                .id(cartEntity.getId())
                .itemResponseList(new ArrayList<>())
                .build();
        List<ItemResponse> itemResponseList = new ArrayList<>();
        for(ItemEntity itemEntity: cartEntity.getItemEntityList()){
            BookEntity bookEntity = itemEntity.getBookEntity();
            BookResponse bookResponse = BookResponse.builder()
                    .id(bookEntity.getId())
                    .title(bookEntity.getTitle())
                    .author(bookEntity.getAuthor())
                    .type(bookEntity.getType())
                    .price(bookEntity.getPrice())
                    .imagesStringUrl(bookEntity.getImageEntityList().stream()
                            .map(ImageEntity::getUrl)
                            .collect(Collectors.toList()))
                    .build();
            ItemResponse itemResponse = ItemResponse.builder()
                    .id(itemEntity.getId())
                    .quantity(itemEntity.getQuantity())
                    .status(itemEntity.getStatus())
                    .bookResponse(bookResponse)
                    .build();
            itemResponseList.add(itemResponse);
        }
        cartResponse.setItemResponseList(itemResponseList);
        return cartResponse;
    }

    @Transactional
    @CachePut(value = "carts", key = "#cartId")
    public void addItem(ItemDTO itemDTO, Long cartId) {
        try{
            itemRepository.insert(
                    itemDTO.getQuantity(),
                    itemDTO.getStatus(),
                    itemDTO.getBookId(),
                    cartId);
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Override
    public void updateItem(ItemDTO itemDTO, Long itemId) {
        try{
            itemRepository.updateQuantity(itemId, itemDTO.getQuantity());
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Override
    public void deleteItem(Long itemId) {
        try{
            itemRepository.deleteById(itemId);
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }
}
