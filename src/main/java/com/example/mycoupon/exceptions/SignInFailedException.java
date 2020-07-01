package com.example.mycoupon.exceptions;

import org.springframework.security.core.AuthenticationException;

public class SignInFailedException extends AuthenticationException {
    public SignInFailedException(String msg, Throwable t) {
        super(msg, t);
    }
}
