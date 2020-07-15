package com.example.mycoupon.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class AopAspect {
    //@Around(value="@annotation(LogExecutionTime) && args(member)", argNames = "member")
    @Around(value="@annotation(LogExecutionTime)")
    public Object LogExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(); // method return value
        long executionTime = System.currentTimeMillis() - startTime;

        log.info(joinPoint.getSignature() + " executed in " + executionTime);
        //log.info("ID: " + member.getMediaId() + " assigned to coupon(" + proceed + ")");
        return proceed;
    }
}
