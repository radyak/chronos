package net.fvogel.chronosbackend.commons.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@NoArgsConstructor
public class ConflictingDataException extends RuntimeException {
    public ConflictingDataException(String message) {
        super(message);
    }
}
