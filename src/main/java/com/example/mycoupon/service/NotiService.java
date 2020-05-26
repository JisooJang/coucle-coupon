package com.example.mycoupon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotiService {
    @Async
    public void sendNoticeToUser(String userId) {
        // 테스트용으로 로그만 찍음. 향후 알림톡을 보내거나 메시지 큐 구조로 메시지를 쏘는 등의 작업이 될 수 있다.
        log.info("Your coupon expires in 3 days. userId : " + userId + " - " + Thread.currentThread().getName());
    }
}
