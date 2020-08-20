package com.example.mycoupon.exceptions;

public class InternalFailureException extends RuntimeException {

    public InternalFailureException(Throwable cause) {
        super("unknown server error : " + cause.getLocalizedMessage(), cause);
    }
}
