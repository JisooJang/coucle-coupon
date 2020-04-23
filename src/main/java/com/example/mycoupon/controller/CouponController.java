package com.example.mycoupon.controller;

import com.example.mycoupon.domain.coupon.Coupon;
import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.domain.coupon.CouponService;
import com.example.mycoupon.domain.member.MemberService;
import com.example.mycoupon.exceptions.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;


/*
TODO:
JwyAuthorizationFilter 로직을 컨트롤러 진입 전에 먼저 타서 헤더의 토큰이 유효한 지 검사 후 ->
토큰이 유효하면, 토큰을 까서 user_id를 컨트롤러로 리턴한다.
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponservice;
    private final MemberService memberService;

    @Autowired
    public CouponController(CouponService couponService, MemberService memberService) {
        this.couponservice = couponService;
        this.memberService = memberService;
    }

    // 1. 랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.
    @PostMapping("/{num}")
    public ResponseEntity<?> saveCoupon(@PathVariable("num") int num, @RequestAttribute long memberId) {
        if(num >= 100) {
            return ResponseEntity.badRequest().build();
        }
        couponservice.bulkSave(num);

        URI selfLink = URI.create(
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString()
        );
        return ResponseEntity.created(selfLink).build();
    }

    // 2. 생성된 쿠폰 중 하나를 사용자에게 지급하는 API를 구현하세요.
    @PutMapping("/user")
    public ResponseEntity<String> assignToUserCoupon(@RequestAttribute("memberId") long memberId) throws MemberNotFoundException { // user_id는 JwtAuthorizationFilter에서 넘겨줌.
        Optional<Member> member = memberService.findById(memberId);
        if(member.isPresent()) {
            return ResponseEntity.ok(couponservice.assignToUser(member.get()));
        } else {
            throw new MemberNotFoundException(memberId);
        }
    }

    // 3. 사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.
    @GetMapping("/user")
    public ResponseEntity<List<Coupon>> getUsersCoupons(@RequestAttribute("memberId") long memberId) throws MemberNotFoundException {
        List<Coupon> coupons = couponservice.findByMember(memberId);
        if(coupons == null || coupons.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coupons);
    }

    // 4. 지급된 쿠폰 중 하나를 사용하는 API를 구현하세요. (쿠폰 재사용은 불가)
    @PutMapping("/{coupon_code}")
    public ResponseEntity<?> useCoupon(@PathVariable("coupon_code") String couponCode,
                                       @RequestAttribute("memberId") long memberId) throws CouponNotFoundException {
        couponservice.updateIsEnabledCouponById(couponCode, memberId, false);
        return ResponseEntity.ok().build();
    }

    // 5. 지급된 쿠폰 중 하나를 사용 취소하는 API를 구현하세요. (취소된 쿠폰 재사용 가능)
    @PutMapping("/{coupon_code}/cancel")
    public ResponseEntity<?> cancelUseCoupon(@PathVariable("coupon_code") String couponCode,
                                             @RequestAttribute("memberId") long memberId) throws CouponNotFoundException {
        couponservice.updateIsEnabledCouponById(couponCode, memberId,true);
        return ResponseEntity.ok().build();
    }

    //6. 발급된 쿠폰 중 당일 만료된 전체 쿠폰 목록을 조회하는 API를 구현하세요.
    @GetMapping("/expired")
    public ResponseEntity<List<Coupon>> getExpiredCoupon() {
        List<Coupon> coupons = couponservice.findExpiredToday();
        if(coupons == null || coupons.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coupons);
    }

}
