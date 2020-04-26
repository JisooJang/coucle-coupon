package com.example.mycoupon.repository;

import com.example.mycoupon.domain.CouponInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponInfoRepository extends JpaRepository<CouponInfo, Long> {
}
