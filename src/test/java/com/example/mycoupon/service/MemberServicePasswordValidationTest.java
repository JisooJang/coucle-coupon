package com.example.mycoupon.service;

import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.repository.MemberRepository;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(JUnitParamsRunner.class)
public class MemberServicePasswordValidationTest {
    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @Before
    public void prepare() {
        this.memberService = new MemberService(memberRepository, new BCryptPasswordEncoder());
    }


    @Test
    @Parameters({
            "qwer1234!", "asdf1234!!", "aQsWdE411!", "testQWER1234@@", "fakePassword123!@"
    })
    public void validationPassword(String password) {
        memberService.validationPassword(password);
    }

    @Test(expected = InvalidPayloadException.class)
    @Parameters({
            "qwer1234", "11234!!", "qqq223wwr", "test12344", "!@wqqwrRR"
    })
    public void validationPasswordFailed(String password) {
        memberService.validationPassword(password);
    }
}
