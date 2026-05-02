package net.fvogel.chronos.schema.config.web;

import jakarta.servlet.http.HttpServletRequest;
import net.fvogel.chronos.commons.rest.exceptionhandling.ErrorResponse;
import net.fvogel.chronos.commons.rest.exceptionhandling.ErrorResponseErrorDetail;
import net.fvogel.chronos.commons.rest.exceptionhandling.RestExceptionHandler;
import net.fvogel.chronos.schema.domain.schema.business.ValidationError;
import net.fvogel.chronos.schema.domain.schema.business.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest req) {
        logger.debug("Encountered validation errors: {}", ex.getMessage(), ex);
        List<ErrorResponseErrorDetail> errors = ex.getErrors()
                .stream()
                .map(this::mapValidationError)
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

    private ErrorResponseErrorDetail mapValidationError(ValidationError error) {
        return new ErrorResponseErrorDetail(
                error.getField(),
                error.getConstraint(),
                error.getMessage(),
                error.getArguments()
        );
    }

}
