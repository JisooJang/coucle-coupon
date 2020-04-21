package com.example.mycoupon.repository;

import com.example.mycoupon.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(String memberId);
}
