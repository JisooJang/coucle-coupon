package com.example.mycoupon.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.mycoupon.config.security.JWTSecurityConstants;
import com.example.mycoupon.controller.CouponController;
import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.service.CouponService;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.service.MemberService;
import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CouponController.class)
@OverrideAutoConfiguration(enabled=true)
public class CouponControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponRepository couponRepository;

    private final Calendar calendar = Calendar.getInstance();

    private String getJWT() {
        Date nowDate = new Date();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE, 1);

        Date expiredAt = calendar.getTime();
        return JWT.create()
                .withIssuer("MyCoupon")
                .withAudience(Long.toString(1L))
                .withSubject("test1234")
                .withIssuedAt(nowDate)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC512(JWTSecurityConstants.SECRET.getBytes()));
    }

    private String getExpiredJWT() {
        Date nowDate = new Date();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE,-1);

        Date expiredAt = calendar.getTime();

        return JWT.create()
                .withIssuer("MyCoupon")
                .withAudience(Long.toString(1L))
                .withSubject("test1234")
                .withIssuedAt(nowDate)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC512(JWTSecurityConstants.SECRET.getBytes()));
    }

    @Test
    public void CouponCreateSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/coupon/100")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void CouponCreateFailureByOverLimit() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/coupon/5000")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void CouponCreateFailureByExpiredToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/coupon/100")
                .header("Authorization", "Bearer " + getExpiredJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponCreateFailureByInvalidToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/coupon/100")
                .header("Authorization", "Bearer " + "fakeInvalidToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponGetUserSuccess() throws Exception {
        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        given(couponService.findByMember(any(Long.class))).willReturn(Collections.singletonList(coupon));
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/user")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void CouponGetUserSuccessNoContent() throws Exception {
        given(couponService.findByMember(any(Long.class))).willReturn(null);
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/user")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void CouponGetUserFailureByExpiredToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/user")
                .header("Authorization", "Bearer " + getExpiredJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponGetUserFailureByInvalidToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/user")
                .header("Authorization", "Bearer " + "fakeInvalidToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponPutUserSuccess() throws Exception {
        Member fakeMember = Member.builder().mediaId("test").password("test1234").build();
        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();

        given(memberService.findById(any(Long.class))).willReturn(Optional.ofNullable(fakeMember));
        given(couponService.assignToUser(fakeMember)).willReturn(coupon.getCode());

        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/user")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void CouponPutUserFailureMemberNotFound() throws Exception {
        given(memberService.findById(any(Long.class))).willReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/user")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void CouponPutUserFailureByExpiredToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/user")
                .header("Authorization", "Bearer " + getExpiredJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponPutUserFailureByInvalidToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/user")
                .header("Authorization", "Bearer " + "fakeInvalidToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponPutUseSuccess() throws Exception {
        String fakeCode = UUID.randomUUID().toString();
        Member fakeMember = Member.builder().mediaId("test").password("test1234").build();
        Coupon coupon = Coupon.builder().code(fakeCode).member(fakeMember).build();

        given(couponRepository.findByCode(fakeCode)).willReturn(coupon);
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void CouponPutUseFailureInvalidCode() throws Exception {
        String fakeCode = "fakeCouponCode";

        doThrow(new InvalidPayloadException("Invalid format of coupon code."))
                .when(couponService).validateCouponCode(fakeCode);
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void CouponPutUseFailureCouponNotFound() throws Exception {
        String fakeCode = UUID.randomUUID().toString();
        doThrow(new CouponNotFoundException(fakeCode))
                .when(couponService).updateIsEnabledCouponById(fakeCode, 1L, true);
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void CouponPutUseFailureCouponUserNotMatch() throws Exception {
        String fakeCode = UUID.randomUUID().toString();
        doThrow(new CouponMemberNotMatchException(fakeCode))
                .when(couponService).updateIsEnabledCouponById(fakeCode, 1L, true);
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect((content().string("This coupon is not matched with request user. : " + fakeCode)));
    }

    @Test
    public void CouponPutUseFailureInvalidToken() throws Exception {
        String fakeCode = UUID.randomUUID().toString();
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + "fakeInvalidToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    public void CouponPutUseFailureExpiredToken() throws Exception {
        String fakeCode = UUID.randomUUID().toString();
        mvc.perform(MockMvcRequestBuilders
                .put("/coupon/" + fakeCode)
                .header("Authorization", "Bearer " + getExpiredJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponGetExpiredTodaySuccess() throws Exception {
        Coupon coupon = Coupon.builder().code(UUID.randomUUID().toString()).build();
        List<Coupon> result = Collections.singletonList(coupon);
        given(couponService.findExpiredToday()).willReturn(result);
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/expired")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(result)));
    }

    @Test
    public void CouponGetExpiredTodaySuccessNoContent() throws Exception {
        given(couponService.findExpiredToday()).willReturn(null);
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/expired")
                .header("Authorization", "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void CouponGetExpiredTodayFailureExpiredToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/expired")
                .header("Authorization", "Bearer " + getExpiredJWT())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void CouponGetExpiredTodayFailureInvalidToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/coupon/expired")
                .header("Authorization", "Bearer " + "fakeInvalidToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}

