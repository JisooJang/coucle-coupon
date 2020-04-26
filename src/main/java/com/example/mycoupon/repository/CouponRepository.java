package com.example.mycoupon.repository;

import com.example.mycoupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // TODO: 1개의 결과값만 받도록 쿼리
    @Query(value = "SELECT * from coupon WHERE member_id IS NULL LIMIT 1", nativeQuery = true)
    //@Query(value = "UPDATE coupon SET user_id = ?1 WHERE id = (SELECT id from coupon WHERE user_id IS NULL LIMIT 1)", nativeQuery = true)
    public Coupon findByFreeUser();

    @Query(value = "SELECT * from coupon WHERE FORMATDATETIME(EXPIRED_AT, 'yyyy-MM-dd') = CURRENT_DATE()", nativeQuery = true)
    public List<Coupon> findByExpiredToday();

    public Coupon findByCode(String code);

    public List<Coupon> findByMemberId(long memberId);
}
