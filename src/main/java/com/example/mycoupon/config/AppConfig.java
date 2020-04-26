package com.example.mycoupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Calendar;

@Configuration
@EnableScheduling
public class AppConfig {
    @Bean
    public Calendar calendar() {
        return Calendar.getInstance();
    }
}
