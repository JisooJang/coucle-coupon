package com.example.mycoupon.controller;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.service.CouponService;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.service.CouponUpdateService;
import com.example.mycoupon.service.MemberService;
import com.example.mycoupon.exceptions.MemberNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CouponControllerTest {
    @Mock
    private CouponService couponService;

    @Mock
    private CouponUpdateService couponUpdateService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    CouponController couponController;

    @Before
    public void prepare() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void saveCoupon() throws Exception {
        long memberId = 1L;
        ResponseEntity<?> result = couponController.saveCoupon(100, memberId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test(expected = InvalidPayloadException.class)
    public void saveCouponOverLimit() throws Exception {
        long memberId = 1L;
        ResponseEntity<?> result = couponController.saveCoupon(1001, memberId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void assignToUserCouponSuccess() throws Exception {
        long targetId = 1L;
        long fakeMemberId = 2L;
        Member fakeMember = Member.builder().mediaId("test1234").password("qwer1234!").build();
        given(this.memberService.findById(targetId)).willReturn(java.util.Optional.ofNullable(fakeMember));

        Coupon fakeCoupon = Coupon.builder().code(UUID.randomUUID().toString()).memberId(targetId).build();
        //given(this.couponService.assignToUserAsync(targetId)).willReturn(fakeCoupon.getCode());
        ResponseEntity<?> result = couponController.assignToUserCouponAsync(fakeMemberId, targetId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fakeCoupon.getCode());
    }

    @Test(expected = MemberNotFoundException.class)
    public void assignToUserCouponMemberNotFound() throws Exception {
        long memberId = 1L;
        Long targetId = 2L;
        Member fakeMember = Member.builder().mediaId("test1234").password("qwer1234!").build();
        given(this.memberService.findById(memberId)).willReturn(Optional.empty());
        ResponseEntity<?> result = couponController.assignToUserCouponAsync(memberId, targetId);
    }

    @Test
    public void getUserCoupons() throws Exception {
        long memberId = 1L;
        Member fakeMember = Member.builder().mediaId("test1234").password("test1234!").build();
        List<Coupon> fakeCoupons = Arrays.asList(
                Coupon.builder().code(UUID.randomUUID().toString()).memberId(memberId).build(),
                Coupon.builder().code(UUID.randomUUID().toString()).memberId(memberId).build());
        given(couponService.findByMember(memberId)).willReturn(fakeCoupons);

        ResponseEntity<?> result = couponController.getUserCoupons(memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fakeCoupons);
    }

    @Test
    public void getUserCouponsNone() throws Exception {
        long memberId = 1L;
        given(couponService.findByMember(memberId)).willReturn(null);

        ResponseEntity<?> result = couponController.getUserCoupons(memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void useCoupon() throws Exception {
        String fakeCouponCode = UUID.randomUUID().toString();
        long memberId = 1L;

        ResponseEntity<?> result = couponController.updateWhetherUsingCoupon(fakeCouponCode, true, memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void cancelUseCoupon() throws Exception {
        String fakeCouponCode = UUID.randomUUID().toString();
        long memberId = 1L;

        ResponseEntity<?> result = couponController.updateWhetherUsingCoupon(fakeCouponCode, false, memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getExpiredCoupon() throws Exception {
        List<Coupon> fakeCoupons = Arrays.asList(
                Coupon.builder().code(UUID.randomUUID().toString()).build(),
                Coupon.builder().code(UUID.randomUUID().toString()).build());

        given(this.couponService.findExpiredToday()).willReturn(fakeCoupons);
        ResponseEntity<?> result = couponController.getExpiredCoupon();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fakeCoupons);
    }

    @Test
    public void getExpiredCouponNone() throws Exception {
        ResponseEntity<?> result = couponController.getExpiredCoupon();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
