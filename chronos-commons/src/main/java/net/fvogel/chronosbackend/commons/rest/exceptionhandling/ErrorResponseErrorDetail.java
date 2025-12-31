package net.fvogel.chronosbackend.commons.rest.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseErrorDetail {
    private String field;
    private String constraint;
    private String message;

    public ErrorResponseErrorDetail(String message) {
        this(null, null, message);
    }
}
