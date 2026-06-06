package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.business.AccountDisabledException;
import com.Man10h.book_store.exception.business.UserNotFoundException;
import com.Man10h.book_store.exception.ErrorException;
import com.Man10h.book_store.model.dto.ChatMessage;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Optional<UserEntity> optional = userRepository.findById(id);
        if(optional.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(!optional.get().getEnabled()){
            throw new AccountDisabledException("Account not enabled");
        }
        try{
            userRepository.updateUserRole(id, 2L);
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if(optional.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        try{
            userRepository.deleteById(id);
        }
        catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "users:username", key = "#username")
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
