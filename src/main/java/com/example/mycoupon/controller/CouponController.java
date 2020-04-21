package com.example.mycoupon.controller;

import com.example.mycoupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponservice;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponservice = couponService;
    }

    // 1. 랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.
    @PostMapping("/")
    public String saveCoupon(@RequestParam("num") int num) {
        return "";
    }

    // 2. 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현하세요.
    @PutMapping("/user")
    public String updateUserCoupon(@RequestParam("user_id") long user_id) { // user_id는 JWT 토큰에서 가져올것.
        return "";
    }

    // 3. 사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.
    @GetMapping("/users")
    public String getUsersCoupons(@RequestParam("user_id") long user_id) {
        return "";
    }

    // 4. 지급된 쿠폰중 하나를 사용하는 API를 구현하세요. (쿠폰 재사용은 불가)
    @PutMapping("/{coupon_id}")
    public String useCoupon(@PathVariable("coupon_id") String coupon_id) {
        return "";
    }

    // 5. 지급된 쿠폰중 하나를 사용 취소하는 API를 구현하세요. (취소된 쿠폰 재사용 가능)
    @PutMapping("/{coupon_id}/cancel")
    public String cancelUseCoupon(@PathVariable("coupon_id") String coupon_id) {
        return "";
    }

    //6. 발급된 쿠폰중 당일 만료된 전체 쿠폰 목록을 조회하는 API를 구현하세요.
    @GetMapping("/expired")
    public String getExpiredCoupon() {
        return "";
    }


}
