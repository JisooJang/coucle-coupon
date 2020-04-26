package com.example.mycoupon.config.security;

import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private MemberRepository memberRepository;

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
