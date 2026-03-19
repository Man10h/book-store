package com.Man10h.book_store.service;

import com.Man10h.book_store.model.entity.UserEntity;

import java.util.Map;

public interface PaymentService {
    public String checkout(UserEntity userEntity);
    public String callback(Map<String, String> params);
}
