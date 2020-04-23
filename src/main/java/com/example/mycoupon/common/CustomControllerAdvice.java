package com.example.mycoupon.common;

import com.example.mycoupon.exceptions.IllegalArgumentException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@ControllerAdvice(annotations = RestController.class)
public class CustomControllerAdvice {
    private final MediaType vndErrorMediaType =
            MediaType.parseMediaType("application/vnd.error");

    @ExceptionHandler(CouponNotFoundException.class)
    ResponseEntity<Object> notFoundException(CouponNotFoundException e) {
        return this.error(e, HttpStatus.NOT_FOUND, e.getCouponCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Object> illegalArgementException(IllegalArgumentException e) {
        return this.error(e, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }

    private <E extends Exception> ResponseEntity<Object> error(E error, HttpStatus httpStatus, String logref) {
        String msg = Optional.of(error.getMessage())
                .orElse(error.getClass().getSimpleName());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(this.vndErrorMediaType);
        return new ResponseEntity<>(logref + ":" + msg,
                httpHeaders, httpStatus);
    }

}
