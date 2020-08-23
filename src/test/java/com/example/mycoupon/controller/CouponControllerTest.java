package com.example.mycoupon.controller;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.service.CouponService;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.service.MemberService;
import com.example.mycoupon.exceptions.MemberNotFoundException;
import com.example.mycoupon.utils.CouponUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.task.TaskExecutor;
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
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CouponControllerTest {
    @Mock
    private CouponService couponService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private CouponController couponController;

    //private Executor executor;


    @Before
    public void prepare() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //executor = Executors.newFixedThreadPool(10);
    }

//    public void doAsync(CompletableFuture<?> future) {
//        this.executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    future.get();
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    @Test
    public void saveCoupon() throws Exception {
        long memberId = 1L;
        Coupon newCoupon = Coupon.builder()
                .code(CouponUtils.getUUIDCouponCode())
                .build();
        given(this.couponService.save()).willReturn(new CompletableFuture<>().completedFuture(newCoupon));
        CompletableFuture<ResponseEntity<?>> result = couponController.saveCoupon(10, memberId);
        assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test(expected = InvalidPayloadException.class)
    public void saveCouponOverLimit() throws Exception {
        long memberId = 1L;
        ResponseEntity<?> result = couponController.saveCoupon(100001, memberId).get();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void assignToUserCouponSuccess() throws Exception {
        long memberId = 1L;
        Member fakeMember = Member.builder().mediaId("test1234").password("qwer1234!").build();
        given(this.memberService.findById(memberId)).willReturn(java.util.Optional.ofNullable(fakeMember));

        Coupon fakeCoupon = Coupon.builder().code(UUID.randomUUID().toString()).member(fakeMember).build();
        given(this.couponService.assignToUserAsync(fakeMember)).willReturn(new CompletableFuture<String>().completedFuture(fakeCoupon.getCode()));
        CompletableFuture<ResponseEntity<String>> result = couponController.assignToUserCouponAsync(memberId);

        assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.get().getBody()).isEqualTo(fakeCoupon.getCode());
    }

    @Test(expected = MemberNotFoundException.class)
    public void assignToUserCouponMemberNotFound() throws Exception {
        long memberId = 1L;
        Member fakeMember = Member.builder().mediaId("test1234").password("qwer1234!").build();
        given(this.memberService.findById(memberId)).willReturn(Optional.empty());
        CompletableFuture<ResponseEntity<String>> result = couponController.assignToUserCouponAsync(memberId);
        assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserCoupons() throws Exception {
        long memberId = 1L;
        Member fakeMember = Member.builder().mediaId("test1234").password("test1234!").build();
        List<Coupon> fakeCoupons = Arrays.asList(
                Coupon.builder().code(UUID.randomUUID().toString()).member(fakeMember).build(),
                Coupon.builder().code(UUID.randomUUID().toString()).member(fakeMember).build());
        given(couponService.findByMember(memberId)).willReturn(Optional.of(fakeCoupons));

        ResponseEntity<?> result = couponController.getUserCoupons(memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fakeCoupons);
    }

    @Test
    public void getUserCouponsNone() throws Exception {
        long memberId = 1L;
        given(couponService.findByMember(memberId)).willReturn(Optional.empty());

        ResponseEntity<?> result = couponController.getUserCoupons(memberId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void useCoupon() throws Exception {
        String fakeCouponCode = UUID.randomUUID().toString();
        long memberId = 1L;

        CompletableFuture<ResponseEntity<?>> result = couponController.updateWhetherUsingCoupon(fakeCouponCode, true, memberId);
        assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void cancelUseCoupon() throws Exception {
        String fakeCouponCode = UUID.randomUUID().toString();
        long memberId = 1L;

        CompletableFuture<ResponseEntity<?>> result = couponController.updateWhetherUsingCoupon(fakeCouponCode, false, memberId);
        assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getExpiredCoupon() throws Exception {
        List<Coupon> fakeCoupons = Arrays.asList(
                Coupon.builder().code(UUID.randomUUID().toString()).build(),
                Coupon.builder().code(UUID.randomUUID().toString()).build());

        given(this.couponService.findExpiredToday()).willReturn(Optional.of(fakeCoupons));
        ResponseEntity<?> result = couponController.getExpiredCoupon();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fakeCoupons);
    }

    @Test
    public void getExpiredCouponNone() throws Exception {
        ResponseEntity<?> result = couponController.getExpiredCoupon();
        given(this.couponService.findExpiredToday()).willReturn(Optional.empty());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
