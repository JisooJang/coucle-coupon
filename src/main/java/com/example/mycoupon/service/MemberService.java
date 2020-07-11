package com.example.mycoupon.service;

import com.example.mycoupon.domain.Member;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.repository.MemberRepository;
import com.example.mycoupon.utils.ValidationRegex;
import com.example.mycoupon.payload.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validationPassword(String password) {
        if(!Pattern.matches(ValidationRegex.PASSWORD, password)) {
            throw new InvalidPayloadException(
                    "password should be use alphabet, number, special-character at least 1 time each." +
                            "And length should be over 8 characters.");
        }
    }

    @Transactional
    public Member signUp(UserModel model) {
        // password 암호화 저장
        // 트랜잭션 레벨 설정
        if(memberRepository.findByMediaId(model.getId()) != null) {
            throw new InvalidPayloadException("user id already exists.");
        }
        validationPassword(model.getPassword());
        Member member = Member.builder()
                .mediaId(model.getId())
                .phoneNumber(model.getPhone_number())
                .password(passwordEncoder.encode(model.getPassword()))
                .build();
        try {
            return memberRepository.save(member);
        } catch(ConstraintViolationException ex) {
            throw new InvalidPayloadException("Invalid arguments.");
        }

    }

    @Transactional(readOnly = true)
    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }
}
