package mar.sirenko.task_system.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ) {
        log.error("Handle handleGenericException", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponseDto(
                                "Internal server error",
                                e.getMessage(),
                                LocalDateTime.now()
                        )
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(
            EntityNotFoundException e
    ) {
        log.error("Handle handleEntityNotFound", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body( new ErrorResponseDto(
                        "Entity not found",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(exception = {
            IllegalStateException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            Exception e
    ) {
        log.error("Handle handleBedRequest", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponseDto(
                                "Bad request",
                                e.getMessage(),
                                LocalDateTime.now()
                        )
                );
    }

}
