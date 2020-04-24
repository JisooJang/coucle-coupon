package com.example.mycoupon.coupon;

import com.example.mycoupon.domain.coupon.CouponService;
import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import com.example.mycoupon.domain.coupon.CouponRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Calendar;

public class CouponServiceTest {

    @MockBean
    private CouponRepository couponRepository;

    @MockBean
    private CouponInfoRepository couponInfoRepository;

    @MockBean
    private Calendar calendar;

    private CouponService couponService;

    @Before
    public void before() {
        this.couponService = new CouponService(couponRepository, couponInfoRepository, calendar);
    }
    @Test
    public void save() {

    }

    @Test
    public void assignToUser() {

    }

    @Test
    public void updateIsEnabledCouponById() {

    }

    @Test
    public void findExpiredToday() {
        
    }
}
