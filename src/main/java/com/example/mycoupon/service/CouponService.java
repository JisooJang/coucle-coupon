package com.example.mycoupon.service;

import com.example.mycoupon.repository.CouponInfoRepository;
import com.example.mycoupon.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
    }
}
