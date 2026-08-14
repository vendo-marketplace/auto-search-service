package com.vendo.auto_search_service.adapter.auto_search.in.exception;

import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.exception.CategoryNotFoundException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class AutoSearchExceptionHandler {

    @ExceptionHandler(AutoSearchNotFoundException.class)
    ResponseEntity<ExceptionResponse> handleAutoSearchNotFoundException(AutoSearchNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ResponseEntity<ExceptionResponse> handleCategoryNotFoundException(CategoryNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }
}
