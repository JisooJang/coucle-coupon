package com.example.mycoupon.service;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.template.AlarmTalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CouponSchedulerServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @Mock
    private KafkaTemplate<String, AlarmTalk> kafkaTemplate;

    private CouponSchedulerService schedulerService;

    @Before
    public void prepare() {
        this.schedulerService = new CouponSchedulerService(couponRepository, new NotiService(kafkaTemplate));
    }
    @Test
    public void sendNoticeExpiredAfter3days() {
        LocalDateTime nowDateLocal = LocalDateTime.now();
        List<Coupon> couponList = new ArrayList<>();

        for(int i=0 ; i < 100 ; i++) {
            Member m = Member.builder()
                    .mediaId("test" + i)
                    .password("test1234!")
                    .build();

            Coupon coupon = Coupon.builder()
                    .code(UUID.randomUUID().toString())
                    .assignedAt(nowDateLocal)
                    .expiredAt(nowDateLocal.plusDays((long)3))
                    .member(m)
                    .build();
            couponList.add(coupon);
        }

        // mock return data
        given(couponRepository.findByExpiredAfter3Days()).willReturn(couponList);

        schedulerService.sendNoticeExpiredAfter3days();
    }
}
