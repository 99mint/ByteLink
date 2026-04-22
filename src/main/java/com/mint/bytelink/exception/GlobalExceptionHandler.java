package com.mint.bytelink.exception;

import com.mint.bytelink.exception.other.ResourceNotFoundException;
import com.mint.bytelink.exception.other.UrlExpiredException;
import com.mint.bytelink.exception.other.UrlValidationException;
import com.mint.bytelink.exception.other.UserAlreadyExistsException;
import com.mint.bytelink.exception.security.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex){
        ErrorResponse error = new ErrorResponse(404 , ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        ErrorResponse error = new ErrorResponse(400 , "Validation Failed");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpiredUrl(UrlExpiredException ex){
        ErrorResponse error = new ErrorResponse(410 , ex.getMessage());
        return ResponseEntity.status(410).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsUser(UserAlreadyExistsException ex){
        ErrorResponse error = new ErrorResponse(409 , ex.getMessage());
        return ResponseEntity.status(409).body(error);
    }

    @ExceptionHandler(UrlValidationException.class)
    public ResponseEntity<ErrorResponse> handleFakeUrls(UrlValidationException ex){
        ErrorResponse error = new ErrorResponse(400 , ex.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex){
        ErrorResponse error = new ErrorResponse(401 , ex.getMessage());
        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex){
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(500 , "An unexpected error occurred");
        return ResponseEntity.status(500).body(error);
    }
}
