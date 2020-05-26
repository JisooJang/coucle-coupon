package com.example.mycoupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MycouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycouponApplication.class, args);
    }

}
