package com.example.mycoupon.webmvc;

import com.example.mycoupon.config.security.CustomUserDetailsService;
import com.example.mycoupon.controller.CouponController;
import com.example.mycoupon.service.MemberService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@WebMvcTest(CouponController.class)
@OverrideAutoConfiguration(enabled=true)
public class JwtAuthenticationSingUpTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private MemberService memberService;

    @MockBean
    private CustomUserDetailsService userDetailsService;
}
