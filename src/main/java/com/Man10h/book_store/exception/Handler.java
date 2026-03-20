package com.Man10h.book_store.exception;

import com.Man10h.book_store.exception.business.*;
import com.Man10h.book_store.exception.business.IllegalStateException;
import com.Man10h.book_store.exception.client.AuthenticationFailException;
import com.Man10h.book_store.exception.client.InvalidVNPAYSignatureException;
import com.Man10h.book_store.model.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class Handler {

    private ResponseEntity<ErrorMessage> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ErrorMessage.builder()
                        .message(message)
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .build()
        );
    }

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorMessage> handleException(ErrorException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AuthenticationFailException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationFailException(AuthenticationFailException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationDisabledException(AccountDisabledException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<ErrorMessage> handleAccountExistsException(AccountExistsException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleAccountNotFoundException(AccountNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleBookNotFoundException(BookNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleCartNotFoundException(CartNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleItemNotFoundException(ItemNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFoundException(UserNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePaymentNotFoundException(PaymentNotFoundException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(InvalidVNPAYSignatureException.class)
    public ResponseEntity<ErrorMessage> handleInvalidVNPAYNSignatureException(InvalidVNPAYSignatureException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorMessage> handleValidationException(Exception e) {
        String message = "Validation failed";
        if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException
                && methodArgumentNotValidException.getBindingResult().getFieldError() != null) {
            message = methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof BindException bindException
                && bindException.getBindingResult().getFieldError() != null) {
            message = bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorMessage> handleMissingPartException(MissingServletRequestPartException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getRequestPartName() + " is required");
    }
}
