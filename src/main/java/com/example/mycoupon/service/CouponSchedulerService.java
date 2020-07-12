package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.template.AlarmTalk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CouponSchedulerService {
    private final CouponRepository couponRepository;
    private final NotiService notiService;

    @Autowired
    public CouponSchedulerService(CouponRepository repository, NotiService notiService) {
        this.couponRepository = repository;
        this.notiService = notiService;
    }

    // 매일 오후 1시에 실행
    @Scheduled(cron = "0 0 13 * * ?")
    public void sendNoticeExpiredAfter3days() {
        List<Coupon> expiredList = couponRepository.findByExpiredAfter3Days();

        // send message to each user.
        for(Coupon coupon : expiredList) {
            Member member = coupon.getMember();
            AlarmTalk alarmTalk = AlarmTalk.builder()
                    .mediaId(member.getMediaId())
                    .phoneNumber(member.getPhoneNumber())
                    .build();
            notiService.sendAlarmTalkToUser(alarmTalk); // TODO : 비동기 실행
        }
    }
}
