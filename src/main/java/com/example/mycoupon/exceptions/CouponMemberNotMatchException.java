package com.example.mycoupon.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CouponMemberNotMatchException extends RuntimeException {
    private String couponCode;

    public CouponMemberNotMatchException(String couponCode) {
        super("This coupon is not matched with request user. : " + couponCode);
    }
}
