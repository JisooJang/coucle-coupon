package com.example.mycoupon.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class InvalidTokenException extends AuthenticationException {
    private final int statusCode;
    public InvalidTokenException(Throwable t, int statusCode) {
        super("Invalid Authorization Token : " + t.getLocalizedMessage());
        this.statusCode = statusCode;
    }
}
