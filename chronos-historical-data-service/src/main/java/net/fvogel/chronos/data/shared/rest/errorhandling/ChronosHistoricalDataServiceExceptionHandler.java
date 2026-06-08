package net.fvogel.chronos.data.shared.rest.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import net.fvogel.chronos.commons.rest.exceptionhandling.ErrorResponse;
import net.fvogel.chronos.commons.rest.exceptionhandling.RestExceptionHandler;
import net.fvogel.chronos.data.exception.SchemaValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ChronosHistoricalDataServiceExceptionHandler extends RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChronosHistoricalDataServiceExceptionHandler.class);

    @ExceptionHandler(SchemaValidationException.class)
    public ResponseEntity<ErrorResponse> handleSchemaValidationException(SchemaValidationException ex, HttpServletRequest req) {
        logger.debug("Encountered conflicting data: {}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Schema validation",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}
