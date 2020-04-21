package com.example.mycoupon.service;

import com.example.mycoupon.domain.Member;
import com.example.mycoupon.payload.UserModel;
import com.example.mycoupon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUp(UserModel model) {
        // password 암호화 저장
        // 트랜잭션 레벨 설정
        Member member = new Member(model.getId(), model.getPassword());
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }


}
