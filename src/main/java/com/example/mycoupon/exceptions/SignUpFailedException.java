package com.example.mycoupon.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class SignUpFailedException extends AuthenticationException {
    public SignUpFailedException(String msg, Throwable t) {
        super(msg, t);
    }
}
