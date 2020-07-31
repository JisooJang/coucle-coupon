package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.template.AlarmTalk;
import com.example.mycoupon.utils.CountryCode;
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
    @Async
    @Scheduled(cron = "0 0 13 * * ?")
    public void sendNoticeExpiredAfter3days() {
        List<Coupon> expiredList = couponRepository.findByExpiredAfter3Days();

        // send message to each user.
        for(Coupon coupon : expiredList) {
            Member member = coupon.getMember();
            AlarmTalk alarmTalk = AlarmTalk.builder()
                    .countryCode(CountryCode.SOUTH_KOREA)
                    .phoneNumber(member.getPhoneNumber())
                    .message(getExpiredAlarmMessage(member.getMediaId(), coupon.getCode()))
                    .build();
            notiService.sendAlarmTalkToUser(alarmTalk); // TODO : 비동기 실행
        }
    }

    @Async
    @Scheduled(cron = "0 50 21 * * ?")
    public void sendNoticeExpiredAfter3daysForTesting() {
        AlarmTalk alarmTalk = AlarmTalk.builder()
                .countryCode(CountryCode.SOUTH_KOREA)
                .phoneNumber("01038050883")
                .message(getExpiredAlarmMessage("as00314", "ABCD-EFGH-IJKL"))
                .build();
        notiService.sendAlarmTalkToUser(alarmTalk);

    }

    public String getExpiredAlarmMessage(String mediaId, String couponCode) {
        return String.format("%s 회원님! coucle 서비스에서 보유하고 계신 쿠폰이 3일 후에 만료될 예정입니다. [쿠폰코드 : %s]",
                mediaId, couponCode);
    }
}
