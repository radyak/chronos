package net.fvogel.chronosbackend.commons.rest.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseErrorDetail {
    private String field;
    private String constraint;
    private String message;
    private Map<String, Object> arguments;

    public ErrorResponseErrorDetail(String message) {
        this(null, null, message, null);
    }

    public ErrorResponseErrorDetail(String message, Map<String, Object> arguments) {
        this(null, null, message, arguments);
    }
}
