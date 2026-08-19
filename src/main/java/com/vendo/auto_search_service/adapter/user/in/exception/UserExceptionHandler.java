package com.vendo.auto_search_service.adapter.user.in.exception;

import com.vendo.auto_search_service.domain.user.exception.UserNotOwnerException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotOwnerException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotOwnerException(UserNotOwnerException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

}
