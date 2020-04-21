package com.example.mycoupon.security;

import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository; // 필드 주입 문제 없을 지 생각해 볼 것

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId);
        if(member == null) {
            throw new UsernameNotFoundException("Cannot find user.");
        } else {
            return new SecurityMember(member);
        }
    }
}
