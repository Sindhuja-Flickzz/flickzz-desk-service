package com.flickzz.desk.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FlickzzDeskExceptionHandlerTest {

    private final FlickzzDeskExceptionHandler handler = new FlickzzDeskExceptionHandler();

    @Test
    void shouldReturnConflictResponseForDataIntegrityViolation() {
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("duplicate key value violates unique constraint");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(FlickzzDeskErrorCodes.DB_SAVE_ERROR.getCode());
        assertThat(response.getBody().getDescription()).contains("database constraint");
    }
}
