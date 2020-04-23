package com.example.mycoupon.domain.coupon;

import com.example.mycoupon.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // TODO: 1개의 결과값만 받도록 쿼리
    @Query(value = "SELECT * from coupon WHERE user_id IS NULL LIMIT 1", nativeQuery = true)
    //@Query(value = "UPDATE coupon SET user_id = ?1 WHERE id = (SELECT id from coupon WHERE user_id IS NULL LIMIT 1)", nativeQuery = true)
    public Coupon findByFreeUser();

    public Coupon findByCode(String code);

    @Query(value = "SELECT * from coupon WHERE DATE(expiredAt) = DATE(NOW())", nativeQuery = true)
    public Optional<List<Coupon>> findByExpiredToday();
}
