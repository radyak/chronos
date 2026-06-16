package net.fvogel.chronos.data.shared.rest.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import net.fvogel.chronos.commons.rest.exceptionhandling.ErrorResponse;
import net.fvogel.chronos.commons.rest.exceptionhandling.ErrorResponseErrorDetail;
import net.fvogel.chronos.commons.rest.exceptionhandling.RestExceptionHandler;
import net.fvogel.chronos.data.exception.SchemaValidationException;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ChronosHistoricalDataServiceExceptionHandler extends RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChronosHistoricalDataServiceExceptionHandler.class);

    @ExceptionHandler(SchemaValidationException.class)
    public ResponseEntity<ErrorResponse> handleSchemaValidationException(SchemaValidationException ex, HttpServletRequest req) {
        logger.debug("Encountered error while validating data: {}", ex.getMessage(), ex);
        List<ErrorResponseErrorDetail> errors = ex.getValidationErrors()
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
        // TODO: Adjust a little better; e.g. ad message to Constraints
        return new ErrorResponseErrorDetail(
                error.getPath(),
                error.getConstraint().toString(),
                error.getConstraint().i18n(),
                Map.of("value", error.getValue())
        );
    }

}
