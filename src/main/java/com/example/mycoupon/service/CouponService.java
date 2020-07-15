package com.example.mycoupon.service;

import com.example.mycoupon.aop.LogExecutionTime;
import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.exceptions.InvalidPayloadException;
import com.example.mycoupon.repository.CouponRepository;
import com.example.mycoupon.utils.ValidationRegex;
import com.example.mycoupon.domain.CouponInfo;
import com.example.mycoupon.domain.Member;
import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.repository.CouponInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
    }

    public void validateCouponCode(String code) throws InvalidPayloadException {
        if(!Pattern.matches(ValidationRegex.COUPONCODE, code)) {
            throw new InvalidPayloadException("Invalid format of coupon code.");
        }
    }

    public String getUUIDCouponCode() {
        // format : uuid
        return UUID.randomUUID().toString();
    }

    public LocalDateTime getRandomExpiredAt(LocalDateTime fromDate) {
        // add random expired days from now date (1 day ~ 7 days)
        return fromDate.plusDays((long)(Math.random() * 7) + 1);
    }

    // 트랜잭션 전파 유형 : PROPAGATION_REQUIRED (기본값)
    // 이미 존재하는 부모 트랜잭션이 있다면 부모 트랜잭션 내에서 실행되고, 부모 트랜잭션이 없다면 새 트랜잭션이 시작된다.
    @Async
    @Transactional
    public CompletableFuture<Coupon> save() {
        return CompletableFuture.supplyAsync(() -> {
            LocalDateTime nowDateLocal = LocalDateTime.now();
            Coupon coupon = Coupon.builder()
                    .code(getUUIDCouponCode())
                    .build();

            Coupon couponResult = couponRepository.save(coupon);
            CouponInfo couponInfo = CouponInfo.builder()
                    .couponId(couponResult.getId())
                    .isUsed(false)
                    .build();

            couponInfoRepository.save(couponInfo);
            return couponResult;
        });
    }

    @Transactional
    public Coupon saveNewCouponByMember(Member member) {
        LocalDateTime nowDateLocal = LocalDateTime.now();
        Coupon coupon = Coupon.builder()
                .code(getUUIDCouponCode())
                .assignedAt(nowDateLocal)
                .expiredAt(getRandomExpiredAt(nowDateLocal))
                .member(member)
                .build();

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder()
                .couponId(couponResult.getId())
                .isUsed(false)
                .build();

        couponInfoRepository.save(couponInfo);
        return couponResult;
    }

    @Transactional
    public Coupon updateCouponByMember(Coupon coupon, Member member) {
        LocalDateTime assignedAt = LocalDateTime.now();
        coupon.setMember(member);  // update SQL
        coupon.setAssignedAt(assignedAt);
        coupon.setExpiredAt(getRandomExpiredAt(assignedAt));
        return coupon;
    }

    @Async
    @CacheEvict(value="coupon-list", key="#member.id")
    @LogExecutionTime
    public CompletableFuture<String> assignToUserAsync(Member member) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync(() -> {
            log.info("current Thread name : " + Thread.currentThread().getName());

            Optional<Coupon> coupon = couponRepository.findByFreeUser();
            Coupon couponResult;
            if(!coupon.isPresent()) {
                couponResult = saveNewCouponByMember(member);
            } else {
                couponResult = updateCouponByMember(coupon.get(), member);
            }
            return couponResult.getCode();
        }).exceptionally((ex) -> {
            log.error(ex.getLocalizedMessage());
            // TODO: change definite exception class.
            throw new RuntimeException(ex.getLocalizedMessage());
        });
    }

    @Transactional
    public void updateIsEnabledCouponById(String code, long memberId, boolean isUsed) throws CouponNotFoundException {
        Coupon coupon = couponRepository.findByCode(code);
        if(coupon == null) {
            throw new CouponNotFoundException(code);
        }
        if(coupon.getMember().getId() != memberId) {
            throw new CouponMemberNotMatchException(code);
        }
        coupon.getCouponInfo().setIsUsed(isUsed);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findExpiredToday() {
        return couponRepository.findByExpiredToday();
    }

    @Cacheable(value="coupon-list", key="#memberId")
    @Transactional(readOnly = true)
    public List<Coupon> findByMember(long memberId) {
        log.info("@Cacheable findByMember method called.");
        return couponRepository.findByMemberNotUsed(memberId);
    }
}
