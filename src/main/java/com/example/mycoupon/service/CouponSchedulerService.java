package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CouponSchedulerService {
    private final CouponRepository couponRepository;

    @Autowired
    public CouponSchedulerService(CouponRepository repository) {
        this.couponRepository = repository;
    }

    // 매일 오후 1시에 실행
    @Scheduled(cron = "0 0 13 * * ?")
    public void sendNoticeExpiredAfter3days() {
        List<Coupon> expiredList = couponRepository.findByExpiredAfter3Days();
        for(Coupon coupon : expiredList) {
            log.info("Your coupon expires in 3 days. userId : " + coupon.getMember().getMediaId());
        }
    }
}
