package com.example.mycoupon.config;

import com.example.mycoupon.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 속성으로 특정 컨트롤러 패키지 경로 설정 가능
@RestControllerAdvice
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

    @ExceptionHandler(InvalidPayloadException.class)
    ResponseEntity<Object> invalidPayloadException(InvalidPayloadException e) {
        return this.error(e, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }

    @ExceptionHandler(InternalFailureException.class)
    ResponseEntity<Object> internalFailureException(InternalFailureException e) {
        return this.error(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
    }

    private <E extends Exception> ResponseEntity<Object> error(E error, HttpStatus httpStatus, String msg) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(this.vndErrorMediaType);
        return new ResponseEntity<>(msg,
                httpHeaders, httpStatus);
    }

}
