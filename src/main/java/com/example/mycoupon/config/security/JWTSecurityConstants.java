package com.example.mycoupon.config.security;

public class JWTSecurityConstants {
    public static final String SECRET = "2179674B92C14B02B304FA330C5050FCADBA5F034CF7EC1E9E41D7FACED9A9FA";

    public static final long EXPIRATION_TIME = 86400000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
