package com.example.mycoupon.coupon;

import com.example.mycoupon.domain.coupon.CouponService;
import com.example.mycoupon.domain.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class CouponControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private MemberService memberService;
}
