package com.example.mycoupon.domain.member;

import com.example.mycoupon.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMediaId(String mediaId);
}
