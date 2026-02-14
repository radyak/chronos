package net.fvogel.chronos.commons.rest.exceptionhandling;

import jakarta.servlet.http.HttpServletRequest;
import net.fvogel.chronos.commons.exception.ConflictingDataException;
import net.fvogel.chronos.commons.exception.InvalidDataException;
import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.commons.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ConflictingDataException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(ConflictingDataException ex, HttpServletRequest req) {
        logger.debug("Encountered conflicting data: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Data conflict",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException ex, HttpServletRequest req) {
        logger.debug("Encountered invalid data: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Received invalid data",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex, HttpServletRequest req) {
        logger.debug("Encountered invalid parameter: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Received invalid parameter",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        logger.debug("Resource not found: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Resource not found",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        logger.error("Unhandled error for request {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please contact support.",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
