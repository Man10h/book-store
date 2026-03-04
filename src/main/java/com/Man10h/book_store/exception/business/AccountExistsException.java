package com.Man10h.book_store.exception.business;

public class AccountExistsException extends RuntimeException {
    public AccountExistsException(String message) {
        super(message);
    }
}
