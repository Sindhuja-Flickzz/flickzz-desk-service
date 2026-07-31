package com.flickzz.desk.exception;

import org.hibernate.TransientPropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlickzzDeskExceptionHandler {

    @ExceptionHandler(FlickzzDeskException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(FlickzzDeskException ex) {
        HttpStatus status = ex.getErrorCode() == FlickzzDeskErrorCodes.DB_SAVE_ERROR
                ? HttpStatus.CONFLICT
                : HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getTitle(),
                ex.getDescription().isBlank() ? ex.getErrorCode().getDescription() : ex.getDescription()
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse response = new ErrorResponse(
                FlickzzDeskErrorCodes.DB_SAVE_ERROR.getCode(),
                FlickzzDeskErrorCodes.DB_SAVE_ERROR.getTitle(),
                "Unable to save the record because a database constraint was violated. Please verify the input data and try again."
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TransientPropertyValueException.class)
    public ResponseEntity<ErrorResponse> handleTransientPropertyValueException(TransientPropertyValueException ex) {
        ErrorResponse response = new ErrorResponse(
                FlickzzDeskErrorCodes.DB_SAVE_ERROR.getCode(),
                FlickzzDeskErrorCodes.DB_SAVE_ERROR.getTitle(),
                "Unable to delete the record because it is referenced by existing records. Please remove those associations and try again."
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
