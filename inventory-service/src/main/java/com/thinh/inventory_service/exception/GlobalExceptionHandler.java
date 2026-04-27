package com.thinh.inventory_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", e.getErrorCode().name());
        response.put("message", e.getMessage());
        
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(response);
    }
}
