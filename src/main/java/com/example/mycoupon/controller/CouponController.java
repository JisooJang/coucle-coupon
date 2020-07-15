package com.example.mycoupon.controller;

import com.example.mycoupon.aop.LogExecutionTime;
import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.service.CouponService;
import com.example.mycoupon.service.MemberService;
import com.example.mycoupon.exceptions.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
JwyAuthorizationFilter 로직을 컨트롤러 진입 전에 먼저 타서 헤더의 토큰이 유효한 지 검사 후 ->
토큰이 유효하면, memberId를 Request attribute에 담아 컨트롤러로 전달한다.
 */
@Slf4j
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

    @LogExecutionTime
    @PostMapping("/{num}")
    public ResponseEntity<?> saveCoupon(@PathVariable("num") int num, @RequestAttribute long memberId) throws InterruptedException {
        if(num > 100000) {
            throw new InvalidPayloadException("The number of coupon should be less than 1000000.");
        }

        List<CompletableFuture<Coupon>> futureList = IntStream.range(0, num)
                .mapToObj(i -> couponservice.save())
                .collect(Collectors.toList());

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[num]))
                .thenAccept(s -> log.info("Computation returned: " + s));

        URI selfLink = URI.create(
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString()
        );
        return ResponseEntity.created(selfLink).build();
    }

    @PutMapping("/user")
    public ResponseEntity<String> assignToUserCouponAsync(@RequestAttribute("memberId") long memberId) throws MemberNotFoundException, ExecutionException, InterruptedException { // user_id는 JwtAuthorizationFilter에서 넘겨줌.
        Optional<Member> member = memberService.findById(memberId);
        if(member.isPresent()) {
            CompletableFuture<String> futureResult = couponservice.assignToUserAsync(member.get());
            return ResponseEntity.ok().body(futureResult.get());
        } else {
            throw new MemberNotFoundException(memberId);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Coupon>> getUserCoupons(@RequestAttribute("memberId") long memberId) throws MemberNotFoundException {
        List<Coupon> coupons = couponservice.findByMember(memberId);
        if(coupons == null || coupons.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coupons);
    }

    @PutMapping("/{coupon_code}")
    public ResponseEntity<?> useCoupon(@PathVariable("coupon_code") String couponCode,
                                       @RequestAttribute("memberId") long memberId) throws CouponNotFoundException {
        couponservice.validateCouponCode(couponCode);
        couponservice.updateIsEnabledCouponById(couponCode, memberId, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{coupon_code}/cancel")
    public ResponseEntity<?> cancelUseCoupon(@PathVariable("coupon_code") String couponCode,
                                             @RequestAttribute("memberId") long memberId) throws CouponNotFoundException {
        couponservice.validateCouponCode(couponCode);
        couponservice.updateIsEnabledCouponById(couponCode, memberId,false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Coupon>> getExpiredCoupon() {
        List<Coupon> coupons = couponservice.findExpiredToday();
        if(coupons == null || coupons.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coupons);
    }

}
