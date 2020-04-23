package com.example.mycoupon.domain.couponInfo;

import com.example.mycoupon.domain.couponInfo.CouponInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponInfoRepository extends JpaRepository<CouponInfo, Long> {
//    @Query(value = "UPDATE couponinfo SET is_enabled = :isEnabled WHERE code = :code", nativeQuery = true)
//    public void updateByCode(@Param("code") String code, @Param("isEnabled") boolean isEnabled);
}
