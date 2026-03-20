package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.business.BookNotFoundException;
import com.Man10h.book_store.exception.business.ItemNotFoundException;
import com.Man10h.book_store.exception.ErrorException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.*;
import com.Man10h.book_store.model.response.BookResponse;
import com.Man10h.book_store.model.response.CartResponse;
import com.Man10h.book_store.model.response.ItemResponse;
import com.Man10h.book_store.repository.BookRepository;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.ItemRepository;
import com.Man10h.book_store.service.CartService;
import lombok.RequiredArgsConstructor;
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
    private final BookRepository bookRepository;

    @Override
    public CartResponse getCart(UserEntity userEntity) {
        CartEntity cartEntity = getOrCreateCart(userEntity);
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
    public void addItem(UserEntity userEntity, ItemDTO itemDTO, Long id) {
        Optional<BookEntity> optionalBookEntity = bookRepository.findById(id);
        if(optionalBookEntity.isEmpty()){
            throw new BookNotFoundException("Book not found");
        }
        CartEntity cartEntity = getOrCreateCart(userEntity);

        ItemEntity itemEntity = ItemEntity.builder()
                .bookEntity(optionalBookEntity.get())
                .quantity(itemDTO.getQuantity())
                .cartEntity(cartEntity)
                .status(itemDTO.getStatus())
                .build();
        itemRepository.save(itemEntity);
    }

    @Transactional
    public void updateItem(ItemDTO itemDTO, Long itemId, UserEntity userEntity) {
        try{
            CartEntity cartEntity = getOrCreateCart(userEntity);
            if(itemRepository.findItemInCart(itemId, cartEntity.getId()).isEmpty()){
                throw new ItemNotFoundException("Item not found in cart");
            }
            itemRepository.updateQuantity(itemId, itemDTO.getQuantity());
        }catch (ItemNotFoundException e) {
            throw new ItemNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Transactional
    public void deleteItem(Long itemId, UserEntity userEntity) {
        try{
            CartEntity cartEntity = getOrCreateCart(userEntity);
            ItemEntity itemEntity = itemRepository.findItemInCart(itemId, cartEntity.getId())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found in cart"));

            if (cartEntity.getItemEntityList() != null) {
                cartEntity.getItemEntityList().removeIf(item -> itemId.equals(item.getId()));
            }
            itemEntity.setCartEntity(null);
            itemRepository.delete(itemEntity);
            itemRepository.flush();
        } catch (ItemNotFoundException e) {
            throw new ItemNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    private CartEntity getOrCreateCart(UserEntity userEntity) {
        List<CartEntity> cartEntityList = cartRepository.findByUserEntity(userEntity);
        if (!cartEntityList.isEmpty()) {
            return cartEntityList.getFirst();
        }

        CartEntity cartEntity = CartEntity.builder()
                .itemEntityList(new ArrayList<>())
                .userEntity(userEntity)
                .build();
        return cartRepository.save(cartEntity);
    }
}
