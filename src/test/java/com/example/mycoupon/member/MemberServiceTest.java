package com.example.mycoupon.member;

import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.domain.member.MemberRepository;
import com.example.mycoupon.domain.member.MemberService;
import com.example.mycoupon.payload.UserModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @Before
    public void prepare() {
        this.memberService = new MemberService(memberRepository, new BCryptPasswordEncoder());
    }
    @Test
    public void signUp() {
        String testId = "test1234";
        String testPw = "test1234!";

        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        memberService.signUp(model);
    }

    // TODO: parametrize 가능한지?
    @Test()
    public void signUpValidationFailed() {
        UserModel model = new UserModel();
        model.setId("test1234");
        model.setPassword("test1234!");
        memberService.signUp(model);
    }

    @Test()
    public void signUpIdAlreadyExists() {
        UserModel model = new UserModel();
        model.setId("test1234");
        model.setPassword("test1234!");
        memberService.signUp(model);

        UserModel model2 = new UserModel();
        model2.setId("test1234");
        model2.setPassword("test1234!!");
        memberService.signUp(model2);
    }

    @Test
    public void findById() {
        long fakeId = 1L;
        given(memberRepository.findById(fakeId)).willReturn(
                Optional.ofNullable(Member.builder().mediaId("test1234").password("test1234!").build()));
        Optional<Member> m = memberService.findById(fakeId);
        assertThat(m).isNotNull();
    }

    @Test
    public void findByIdNull() {
        Optional<Member> m = memberService.findById(1L);
        assertThat(m.isPresent()).isFalse();
    }
}
