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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Before
    public void prepare() {
        this.couponService = new CouponService(couponRepository, couponInfoRepository);
    }

    @Test
    public void save() {
        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        given(couponRepository.save(any(Coupon.class))).willReturn(coupon);
        Coupon couponResult = couponService.save(null);

        assertThat(couponResult).isEqualTo(coupon);
    }
    @Test
    public void saveByMember() {
        Member member = Member.builder()
                .mediaId("test1234")
                .password(this.passwordEncoder.encode("qwer1234!"))
                .build();

        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        given(couponRepository.save(any(Coupon.class))).willReturn(coupon);
        Coupon couponResult = couponService.save(member);

        assertThat(couponResult).isEqualTo(coupon);
    }

    @Test
    public void assignToUser() {
        given(this.couponRepository.findByFreeUser()).willReturn(
                Coupon.builder().code("1234").build());

        Member member = Member.builder()
                .mediaId("test1234")
                .password(this.passwordEncoder.encode("qwer1234!"))
                .build();

        String couponCode = couponService.assignToUser(member);
        assertThat(couponCode).isNotNull();
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
                .member(m)
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
        Member m = Member.builder()
                .mediaId("test1234")
                .password(passwordEncoder.encode("test1234!"))
                .build();
        Coupon coupon = Coupon.builder().code(testCode).member(m).build();
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
}
