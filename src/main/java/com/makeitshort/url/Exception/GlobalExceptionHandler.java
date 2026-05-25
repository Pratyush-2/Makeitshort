package com.makeitshort.url.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UrlAlreadyTakenException.class)
    public ResponseEntity<Map<String,Object>> handleUrlAlreadyTaken(UrlAlreadyTakenException ex){
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.CONFLICT.value()); // 409
        errorBody.put("error", "Conflict");
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

}
