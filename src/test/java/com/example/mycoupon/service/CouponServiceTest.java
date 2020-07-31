package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.CouponInfo;
import com.example.mycoupon.repository.CouponInfoRepository;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.CouponNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponInfoRepository couponInfoRepository;

    private CouponService couponService;

    private CouponUpdateService couponUpdateService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Before
    public void prepare() {
        this.couponUpdateService = new CouponUpdateService(couponRepository, couponInfoRepository);
        this.couponService = new CouponService(couponRepository, couponInfoRepository, couponUpdateService);
    }

    @Test
    public void save() throws ExecutionException, InterruptedException {
        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        given(couponRepository.save(any(Coupon.class))).willReturn(coupon);
        CompletableFuture<Coupon> couponResult = couponService.save();

        assertThat(couponResult.get()).isEqualTo(coupon);
    }
    @Test
    public void saveByMember() {
        Long memberId = 1L;
        Member member = Member.builder()
                .mediaId("test1234")
                .password(this.passwordEncoder.encode("qwer1234!"))
                .build();

        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        given(couponRepository.save(any(Coupon.class))).willReturn(coupon);
        Coupon couponResult = couponUpdateService.saveNewCouponByMember(memberId);

        assertThat(couponResult).isEqualTo(coupon);
    }

    @Test
    public void assignToUser() throws InterruptedException, ExecutionException {
        given(this.couponRepository.findByFreeUser().get()).willReturn(
                Coupon.builder().code("1234").build());

        Long memberId = 1L;
        Member member = Member.builder()
                .mediaId("test1234")
                .password(this.passwordEncoder.encode("qwer1234!"))
                .build();

        CompletableFuture<String> couponCode = couponService.assignToUserAsync(memberId);
        assertThat(couponCode.get()).isNotNull();
    }

    @Test(expected = CouponNotFoundException.class)
    public void updateIsEnabledCouponByIdNotFoundCode() {
        String testCode = "test1234";
        given(this.couponRepository.findByCode(testCode)).willReturn(null);
        couponService.updateIsEnabledCouponById(testCode, 1L, true);
    }

    @Test(expected = CouponMemberNotMatchException.class)
    public void updateIsEnabledCouponByIdMemberNotMatch() {
        String testCode = "test1234";
        LocalDateTime tmpDate = LocalDateTime.now();
        Member m = Member.builder().mediaId("testtest").password("test1234!!").build();
        m.setId(2L);

        given(this.couponRepository.findByCode(testCode)).willReturn(
                Coupon.builder()
                .memberId(m.getId())
                .code(testCode)
                .createdAt(tmpDate)
                .assignedAt(tmpDate)
                .expiredAt(tmpDate)
                .build()
        );
        couponService.updateIsEnabledCouponById(testCode, 1L, true);
    }

    @Test
    public void updateIsEnabledCouponById() {
        String testCode = UUID.randomUUID().toString();
        Long memberId = 1L;
        Member m = Member.builder()
                .mediaId("test1234")
                .password(passwordEncoder.encode("test1234!"))
                .build();
        Coupon coupon = Coupon.builder().code(testCode).memberId(memberId).build();
        CouponInfo info = CouponInfo.builder().couponId(coupon.getId()).isUsed(false).build();
        coupon.setCouponInfo(info);
        given(this.couponRepository.findByCode(testCode)).willReturn(coupon);

        couponService.updateIsEnabledCouponById(testCode, m.getId(), true);
    }


    @Test
    public void findExpiredTodayNone() {
        given(couponRepository.findByExpiredToday()).willReturn(null);
        List<Coupon> result = couponService.findExpiredToday();
        assertThat(result).isNull();
    }

    @Test
    public void findExpiredToday() {
        given(couponRepository.findByExpiredToday()).willReturn(
                Collections.singletonList(
                        Coupon.builder().code("test1234").build()));
        List<Coupon> result = couponService.findExpiredToday();
        assertThat(result.size()).isEqualTo(1);
    }

//    @Test
//    public void testTransactionalProxyInnerMethodCall() throws InterruptedException {
//        Member m = Member.builder()
//                .mediaId("test1234")
//                .password(passwordEncoder.encode("test1234!"))
//                .build();
//        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
//        given(couponRepository.findByFreeUser()).willReturn(coupon);
//
//        String memberId = couponService.test(m);
//        System.out.println(memberId);
//    }
}
