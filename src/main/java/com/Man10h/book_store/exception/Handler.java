package com.Man10h.book_store.exception;

import com.Man10h.book_store.exception.business.*;
import com.Man10h.book_store.exception.business.IllegalStateException;
import com.Man10h.book_store.exception.client.AuthenticationFailException;
import com.Man10h.book_store.exception.client.InvalidVNPAYSignatureException;
import com.Man10h.book_store.model.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Handler {



    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorMessage> handleException(ErrorException e) {
        return ResponseEntity.ok(ErrorMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .build());
    }


    @ExceptionHandler(AuthenticationFailException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationFailException(AuthenticationFailException e) {
        return ResponseEntity.ok(ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .build());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationDisabledException(AccountDisabledException e) {
        return ResponseEntity.ok(
          ErrorMessage.builder()
                  .message(e.getMessage())
                  .status(HttpStatus.UNAUTHORIZED.value())
                  .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                  .build()
        );
    }

    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<ErrorMessage> handleAccountExistsException(AccountExistsException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleAccountNotFoundException(AccountNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleBookNotFoundException(BookNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleCartNotFoundException(CartNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleItemNotFoundException(ItemNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePaymentNotFoundException(PaymentNotFoundException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(InvalidVNPAYSignatureException.class)
    public ResponseEntity<ErrorMessage> handleInvalidVNPAYNSignatureException(InvalidVNPAYSignatureException e) {
        return ResponseEntity.ok(
                ErrorMessage.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .build()
        );
    }
}
