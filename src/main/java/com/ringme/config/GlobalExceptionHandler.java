package com.ringme.config;

import com.ringme.dto.record.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        log.error("{} -> request: {} | Exception: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.badRequest().body(new Response(400, e.getMessage()));
    }
}
