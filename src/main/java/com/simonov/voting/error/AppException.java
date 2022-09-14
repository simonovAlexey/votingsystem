package com.simonov.voting.error;

import lombok.Getter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class AppException extends ResponseStatusException {
    private final ErrorAttributeOptions errorAttributeOptions;

    public AppException(HttpStatus status, String message, ErrorAttributeOptions errorAttributeOptions) {
        super(status, message);
        this.errorAttributeOptions = errorAttributeOptions;
    }

    @Override
    public String getMessage() {
        return getReason();
    }
}
