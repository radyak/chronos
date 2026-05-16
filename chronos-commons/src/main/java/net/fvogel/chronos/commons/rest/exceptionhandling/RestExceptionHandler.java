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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationError(MethodArgumentNotValidException ex, HttpServletRequest req) {
        logger.debug("Encountered error while validating data: {}", ex.getMessage(), ex);
        List<ErrorResponseErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                errors,
                "Data is not valid",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
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

    private ErrorResponseErrorDetail mapFieldError(FieldError error) {
        Map<String, Object> params = new HashMap<>();

        Object[] args = error.getArguments();
        if (args != null) {
            if ("Size".equals(error.getCode())) {
                params.put("min", Math.min((Integer) args[1], (Integer) args[2]));
                params.put("max", Math.max((Integer) args[1], (Integer) args[2]));
            }
        }

        return new ErrorResponseErrorDetail(
                error.getField(),
                error.getCode(),
                error.getDefaultMessage(),
                params
        );
    }

}
