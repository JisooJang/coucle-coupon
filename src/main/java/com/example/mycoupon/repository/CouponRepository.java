package com.example.mycoupon.repository;

import com.example.mycoupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query(value = "SELECT * from coupon WHERE member_id IS NULL LIMIT 1", nativeQuery = true)
    Coupon findByFreeUser();

    @Query(value = "SELECT * from coupon WHERE FORMATDATETIME(EXPIRED_AT, 'yyyy-MM-dd') = CURRENT_DATE()", nativeQuery = true)
    List<Coupon> findByExpiredToday();

    Coupon findByCode(String code);

    List<Coupon> findByMemberId(long memberId);

    @Query(value = "SELECT * from coupon WHERE FORMATDATETIME(EXPIRED_AT, 'yyyy-MM-dd') = DATEADD(DAY, 3, CURRENT_DATE())", nativeQuery = true)
    List<Coupon> findByExpiredAfter3Days();
}
