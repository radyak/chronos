package net.fvogel.chronosbackend.config.web;

import jakarta.servlet.http.HttpServletRequest;
import net.fvogel.chronosbackend.commons.rest.exceptionhandling.ErrorResponse;
import net.fvogel.chronosbackend.commons.rest.exceptionhandling.ErrorResponseErrorDetail;
import net.fvogel.chronosbackend.commons.rest.exceptionhandling.RestExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ChronosSchemaDefinitionServiceExceptionHandler extends RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChronosSchemaDefinitionServiceExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        logger.debug("Encountered data violating integrity: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Data integrity violation; contact support for details",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
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
