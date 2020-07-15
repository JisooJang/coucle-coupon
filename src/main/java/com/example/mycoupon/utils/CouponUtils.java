package com.example.mycoupon.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class CouponUtils {
    public CouponUtils() throws Exception {
        throw new Exception("you do not need to construct CouponUtils class!");
    }

    public static String getUUIDCouponCode() {
        // format : uuid
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime getRandomExpiredAt(LocalDateTime fromDate) {
        // add random expired days from now date (1 day ~ 7 days)
        return fromDate.plusDays((long)(Math.random() * 7) + 1);
    }
}
