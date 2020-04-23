package com.example.mycoupon.domain.member;

import com.example.mycoupon.exceptions.IllegalArgumentException;
import com.example.mycoupon.payload.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
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

    public Member signUp(UserModel model) {
        // password 암호화 저장
        // 트랜잭션 레벨 설정
        Member member = new Member(model.getId(), model.getPassword());
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        try {
            return memberRepository.save(member);
        } catch(DataIntegrityViolationException ex) {
            if(ex.getCause() instanceof ConstraintViolationException) {
                throw new IllegalArgumentException("user id already exists.");
            } else {
                throw ex;
            }
        }

    }
    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }
}
