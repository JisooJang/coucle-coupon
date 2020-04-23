package com.example.mycoupon.common;

public class ValidationRegex {
    // 숫자, 문자, 특수문자 각각 1개 이상 포함하여 최소 8자리 이상
    public static final String PASSWORD = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
}
