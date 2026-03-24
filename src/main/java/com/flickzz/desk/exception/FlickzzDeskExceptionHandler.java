package com.flickzz.desk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlickzzDeskExceptionHandler {

    @ExceptionHandler(FlickzzDeskException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(FlickzzDeskException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getTitle(),
                ex.getDescription().isBlank() ? ex.getErrorCode().getDescription() : ex.getDescription()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
