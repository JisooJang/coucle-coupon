package com.example.mycoupon.controller;

import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.IllegalArgumentException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.exceptions.MemberNotFoundException;
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

    @ExceptionHandler(value = {CouponNotFoundException.class, MemberNotFoundException.class})
    ResponseEntity<Object> notFoundException(Exception e) {
        return this.error(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage());
    }

    @ExceptionHandler(value = CouponMemberNotMatchException.class)
    ResponseEntity<Object> couponMemberNotMatchException(Exception e) {
        return this.error(e, HttpStatus.FORBIDDEN, e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Object> illegalArgementException(IllegalArgumentException e) {
        return this.error(e, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }

    private <E extends Exception> ResponseEntity<Object> error(E error, HttpStatus httpStatus, String msg) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(this.vndErrorMediaType);
        return new ResponseEntity<>(msg,
                httpHeaders, httpStatus);
    }

}
