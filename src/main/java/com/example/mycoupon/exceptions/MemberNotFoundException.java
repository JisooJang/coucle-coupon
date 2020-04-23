package com.example.mycoupon.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends RuntimeException {
    private long memberId;

    public MemberNotFoundException(String memberId) {
        super("Member not found : " +memberId);
    }
}
