package com.example.mycoupon.controller;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.service.CouponService;
import com.example.mycoupon.service.MemberService;
import com.example.mycoupon.exceptions.MemberNotFoundException;
import com.example.mycoupon.utils.CouponUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.completedFuture;

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

    @PostMapping("/{num}")
    public CompletableFuture<ResponseEntity<?>> saveCoupon(@PathVariable("num") int num, @RequestAttribute Long memberId) throws InterruptedException {
        if(num > 100000) {
            throw new InvalidPayloadException("The number of coupon should be less than 1000000.");
        }

        List<CompletableFuture<Coupon>> futureList = IntStream.range(0, num)
                .mapToObj(i -> couponservice.save())
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[num]))
                .thenApply((s) -> ResponseEntity.created(
                        URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                ).build());
    }

    @PutMapping("/user")
    public CompletableFuture<ResponseEntity<String>> assignToUserCouponAsync(@RequestAttribute("memberId") Long memberId) throws MemberNotFoundException, ExecutionException, InterruptedException { // user_id는 JwtAuthorizationFilter에서 넘겨줌.
        Optional<Member> member = memberService.findById(memberId);
        if(member.isPresent()) {
            CompletableFuture<String> futureResult =
                    couponservice.assignToUserAsync(member.get());
            return completedFuture(ResponseEntity.ok().body(futureResult.get()));
        } else {
            throw new MemberNotFoundException(memberId);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Coupon>> getUserCoupons(@RequestAttribute("memberId") Long memberId) throws MemberNotFoundException {
        return couponservice.findByMember(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // TODO : 제한 금액 체크. 파라미터로 주문 금액 체크 필요.
    @PutMapping("/{coupon_code}")
    public CompletableFuture<ResponseEntity<?>> updateWhetherUsingCoupon(@PathVariable("coupon_code") String couponCode,
                                       @RequestParam("is_used") Boolean isUsed,
                                       @RequestAttribute("memberId") Long memberId) throws CouponNotFoundException {

        log.info("updateIsEnabledCouponById controller current Thread name : " + Thread.currentThread().getName());
        CouponUtils.validateCouponCode(couponCode);
        return CompletableFuture.runAsync(() ->
                couponservice.updateIsEnabledCouponById(couponCode, memberId, isUsed)
        ).thenApply((s) -> ResponseEntity.ok().build());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Coupon>> getExpiredCoupon() {
        return couponservice.findExpiredToday()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

}
