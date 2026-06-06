package com.Man10h.book_store.services.unit_test;

import com.Man10h.book_store.exception.business.BookNotFoundException;
import com.Man10h.book_store.exception.business.ItemNotFoundException;
import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.ItemEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.repository.BookRepository;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.ItemRepository;
import com.Man10h.book_store.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookRepository bookRepository;

    @Spy
    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    public void should_return_first_cart(){
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();

        CartEntity cartEntity = CartEntity.builder().id(1L).build();
        List<CartEntity> cartEntityList = new ArrayList<>();
        cartEntityList.add(cartEntity);
        when(cartRepository.findByUserEntity(any())).thenReturn(cartEntityList);

        CartEntity cart = cartService.getOrCreateCart(user);
        verify(cartRepository).findByUserEntity(user);
        assertEquals(1L, cart.getId());
    }

    @Test
    public void should_create_cart(){
        List<CartEntity> cartEntityList = new ArrayList<>();
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();

        when(cartRepository.findByUserEntity(any())).thenReturn(cartEntityList);
        cartService.getOrCreateCart(user);

        ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).findByUserEntity(any());
        verify(cartRepository).save(captor.capture());
        assertEquals("test", captor.getValue().getUserEntity().getUsername());
    }


    @Test
    public void should_add_item(){
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(12L)
                .build();
        Long id = 1L;
        BookEntity bookEntity = mock(BookEntity.class);
        CartEntity cartEntity = mock(CartEntity.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        doReturn(cartEntity).when(cartService).getOrCreateCart(user);

        cartService.addItem(user, itemDTO, id);
        ArgumentCaptor<ItemEntity> captor = ArgumentCaptor.forClass(ItemEntity.class);
        verify(itemRepository, times(1)).save(captor.capture());
        verify(bookRepository, times(1)).findById(any());
        verify(cartService, times(1)).getOrCreateCart(any());

        assertEquals(12, captor.getValue().getQuantity());
    }

    @Test
    public void should_not_add_item(){
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(12L)
                .build();
        Long id = 1L;
        when(bookRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> cartService.addItem(user, itemDTO, id));
        verify(bookRepository, times(1)).findById(any());
        verify(cartService, never()).getOrCreateCart(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void should_update_item(){
        Long id = 1L;
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(12L)
                .build();
        CartEntity cartEntity = mock(CartEntity.class);
        doReturn(cartEntity).when(cartService).getOrCreateCart(user);
        ItemEntity itemEntity = mock(ItemEntity.class);
        when(itemRepository.findItemInCart(any(), any())).thenReturn(Optional.of(itemEntity));

        cartService.updateItem(itemDTO, id, user);
        verify(itemRepository, times(1)).findItemInCart(any(), any());
        verify(itemRepository, times(1)).updateQuantity(any(), any());
        verify(cartService, times(1)).getOrCreateCart(any());
    }

    @Test
    public void should_not_update_item(){
        Long id = 1L;
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(12L)
                .build();
        CartEntity cartEntity = mock(CartEntity.class);
        doReturn(cartEntity).when(cartService).getOrCreateCart(user);
        when(itemRepository.findItemInCart(any(), any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> cartService.updateItem(itemDTO, id, user));
        verify(itemRepository, times(1)).findItemInCart(any(), any());
        verify(itemRepository, never()).updateQuantity(any(), any());
        verify(cartService, times(1)).getOrCreateCart(any());
    }

    @Test
    public void should_delete_item(){
        Long id = 1L;
        UserEntity user = UserEntity.builder()
                .id(1L)
                .build();
        ItemEntity itemEntity = ItemEntity.builder()
                .id(1L)
                .build();
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(itemEntity);

        CartEntity cartEntity = CartEntity.builder()
                .id(1L)
                .itemEntityList(itemEntityList)
                .build();
        doReturn(cartEntity).when(cartService).getOrCreateCart(user);
        cartService.deleteItem(id, user);
        assertTrue(itemEntityList.isEmpty());
    }


    @Test
    public void should_throw_exception_when_not_found_item(){
        Long id = 1L;
        UserEntity user = UserEntity.builder()
                .id(1L)
                .build();

        CartEntity cartEntity = mock(CartEntity.class);
        doReturn(cartEntity).when(cartService).getOrCreateCart(user);
        when(itemRepository.findItemInCart(any(), any())).thenReturn(Optional.empty());


        assertThrows(ItemNotFoundException.class, () -> cartService.deleteItem(id, user));
        verify(itemRepository, times(1)).findItemInCart(any(), any());
        verify(itemRepository, never()).delete(any());
    }
}
