package com.example.mycoupon.security;

import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.domain.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    //@Autowired
    private MemberRepository memberRepository; // 필드 주입 문제 없을 지 생각해 볼 것

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mediaId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMediaId(mediaId);
        if(member == null) {
            throw new UsernameNotFoundException("Cannot find user.");
        } else {
            return new SecurityMember(member);
        }
    }
}
