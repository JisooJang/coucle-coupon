package com.example.mycoupon.repository;

import com.example.mycoupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query(value = "SELECT * from coupon WHERE member_id IS NULL LIMIT 1", nativeQuery = true)
    Optional<Coupon> findByFreeUser();

    @Query(value = "SELECT * from coupon WHERE FORMATDATETIME(EXPIRED_AT, 'yyyy-MM-dd') = CURRENT_DATE()", nativeQuery = true)
    List<Coupon> findByExpiredToday();

    Coupon findByCode(String code);

    List<Coupon> findByMemberId(Long memberId);

    @Query(value = "SELECT c from Coupon c WHERE c.member.id = :member_id")
    List<Coupon> findByMemberNotUsed(@Param("member_id") Long memberId);

    // JPQL fetch join (Member entity LAZY Loading)
//    @Query(value = "SELECT c from Coupon c join fetch m.member where c.isUsed = false")
//    List<Coupon> findByMemberNotUsed(@Param("member_id") Long memberId);

    @Query(value = "SELECT * from coupon WHERE FORMATDATETIME(EXPIRED_AT, 'yyyy-MM-dd') = DATEADD(DAY, 3, CURRENT_DATE())", nativeQuery = true)
    List<Coupon> findByExpiredAfter3Days();
}
