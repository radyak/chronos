package net.fvogel.chronosbackend.domain.schema.business;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
@Getter
@NoArgsConstructor
public class ValidationException extends RuntimeException {
    private List<ValidationError> errors = new ArrayList<>();

    public ValidationException(List<ValidationError> errors) {
        super();
        this.errors = errors;
    }
}
