package com.example.mycoupon.member;

import com.example.mycoupon.domain.member.MemberRepository;
import com.example.mycoupon.domain.member.MemberService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MemberServiceTest {
    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @Before
    public void before() {
        this.memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    public void signUp() {

    }

    @Test
    public void findById() {

    }
}
