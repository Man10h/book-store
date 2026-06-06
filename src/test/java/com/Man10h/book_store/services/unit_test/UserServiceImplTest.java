package com.Man10h.book_store.services.unit_test;

import com.Man10h.book_store.exception.business.UserNotFoundException;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void should_delete_user_by_id() {
        UserEntity user = UserEntity.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    public void should_not_delete_user_by_id() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(1L);
        verify(userRepository).findById(1L);
    }


}
