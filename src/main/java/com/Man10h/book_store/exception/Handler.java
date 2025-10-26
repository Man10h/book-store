package com.Man10h.book_store.exception;

import com.Man10h.book_store.exception.exception.BookException;
import com.Man10h.book_store.exception.exception.UserException;
import com.Man10h.book_store.model.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Handler {

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserException(UserException e) {
        return ErrorMessage.builder()
                .message(e.getMessage())
                .code(400)
                .build();
    }

    @ExceptionHandler(BookException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBookException(BookException e) {
        return ErrorMessage.builder()
                .message(e.getMessage())
                .code(400)
                .build();
    }
}
