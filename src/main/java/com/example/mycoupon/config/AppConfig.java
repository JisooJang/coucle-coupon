package com.example.mycoupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Calendar;

@Configuration
public class AppConfig {
    @Bean
    public Calendar calendar() {
        return Calendar.getInstance();
    }
}
