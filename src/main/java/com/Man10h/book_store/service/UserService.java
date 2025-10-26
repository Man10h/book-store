package com.Man10h.book_store.service;

import com.Man10h.book_store.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface UserService {
    public Page<UserResponse> getUsers(Pageable pageable);
    public void updateUserRole(Long id);
    public void deleteUser(Long id);
    public Page<UserResponse> getUsersByUsername(@Param("username") String username, Pageable pageable);
}
