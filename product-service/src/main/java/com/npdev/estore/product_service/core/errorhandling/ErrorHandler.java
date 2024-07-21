package com.npdev.estore.product_service.core.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException e,
                                                                WebRequest webRequest) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage(), new Date()),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    WebRequest webRequest) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage(), new Date()),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(CommandExecutionException.class) // thrown by aggregate
    public ResponseEntity<ApiError> handleCommandExecutionException(CommandExecutionException e,
                                                                    WebRequest webRequest) {
        log.error("Catching an exception {}", e.getMessage(), e);
        return new ResponseEntity<>(
                new ApiError(e.getMessage(), new Date()),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
