package com.example.mycoupon.service;

import com.example.mycoupon.template.AlarmTalk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotiService {
    private final KafkaTemplate<String, AlarmTalk> kafkaTemplate;

    @Autowired
    public NotiService(KafkaTemplate<String, AlarmTalk> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendAlarmTalkToUser(AlarmTalk alarmTalk) {
        log.info("Your coupon expires in 3 days. userNumber : " + alarmTalk.getPhoneNumber() + " - " + Thread.currentThread().getName());
        kafkaTemplate.send("alarmtalk.notification", alarmTalk);

    }
}
