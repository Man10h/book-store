package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.exception.exception.UserException;
import com.Man10h.book_store.model.dto.ChatMessage;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Cacheable(value = "users", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.getUsers(pageable);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void updateUserRole(Long id) {
        try{
            userRepository.updateUserRole(id, 2L);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    @Override
    public Page<UserResponse> getUsersByUsername(String username, Pageable pageable) {
        return userRepository.getUsersByUsername(username, pageable);
    }

    @Override
    public void sendMessage(ChatMessage message) {
        try{
            messagingTemplate.convertAndSendToUser(
                    message.getRecipient(),
                    "/queue/messages",
                    message
            );
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }
}
