package net.fvogel.chronos.commons.rest.exceptionhandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private List<ErrorResponseErrorDetail> errors;
    private String message;
    private String path;

    public ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {
        this(timestamp, status, List.of(new ErrorResponseErrorDetail(error)), message, path);
    }
}
