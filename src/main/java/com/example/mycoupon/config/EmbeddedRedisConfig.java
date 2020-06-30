package com.example.mycoupon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {
    @Value("${spring.redis.port}")
    private int port;

    private RedisServer redisServer = null;

    @PostConstruct
    public void setRedisServer() {
        redisServer = new RedisServer(port);
        redisServer.start();   // start Embedded redis server
    }

    @PreDestroy
    public void stopRedisServer() {
        if(redisServer != null) {
            redisServer.stop();
        }
    }
}
