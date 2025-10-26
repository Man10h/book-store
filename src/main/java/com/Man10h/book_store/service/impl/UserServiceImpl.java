package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.UserException;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.getUsers(pageable);
    }

    @Override
    public void updateUserRole(Long id) {
        try{
            userRepository.updateUserRole(id, 2L);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    @Override
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
}
