package com.Man10h.book_store.service;

import com.Man10h.book_store.model.entity.UserEntity;

public interface TokenService {
    public String generateToken(UserEntity userEntity);
    public boolean validateToken(String token);
    public String getUsername(String token);
}
