package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.CouponInfo;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.CouponInfoRepository;
import com.example.mycoupon.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CouponUpdateService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    @Autowired
    public CouponUpdateService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
    }

    @Transactional
    public Coupon saveNewCouponByMember(Member member) {
        LocalDateTime nowDateLocal = LocalDateTime.now();
        Coupon coupon = Coupon.builder()
                .code(getUUIDCouponCode())
                .assignedAt(nowDateLocal)
                .expiredAt(getRandomExpiredAt(nowDateLocal))
                .member(member)
                .build();

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder()
                .couponId(couponResult.getId())
                .isUsed(false)
                .build();

        couponInfoRepository.save(couponInfo);
        return couponResult;
    }

    // TODO CHECK : JPA Persistence context의 변경 감지 기능 동작해야 함.
    @Transactional // using AOP
    public Coupon updateCouponByMember(Coupon coupon, Member member) {
        LocalDateTime assignedAt = LocalDateTime.now();
        coupon.setMember(member);  // update SQL
        coupon.setAssignedAt(assignedAt);
        coupon.setExpiredAt(getRandomExpiredAt(assignedAt));
        return coupon;
    }

    private LocalDateTime getRandomExpiredAt(LocalDateTime fromDate) {
        return fromDate.plusDays((long)(Math.random() * 7) + 1);
    }

    private String getUUIDCouponCode() {
        return UUID.randomUUID().toString();
    }
}
