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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

    public void bulkSave(int n) {
        for(int i=0 ; i<n ; i++) {
            save(null);
        }
    }

    // 트랜잭션 전파 유형 : PROPAGATION_REQUIRED (기본값)
    // 이미 존재하는 부모 트랜잭션이 있다면 부모 트랜잭션 내에서 실행되고, 부모 트랜잭션이 없다면 새 트랜잭션이 시작된다.
    @Transactional
    public Coupon save(Member member) {
        /* member 매개변수는 null로 넘어올 수 있다. (유저에게 할당하기 전에 쿠폰을 생성할 때) */
        Coupon coupon;
        LocalDateTime nowDateLocal = LocalDateTime.now();
        if(member == null) {
            coupon = Coupon.builder()
                    .code(getUUIDCouponCode())
                    .build();
        } else {
            coupon = Coupon.builder()
                    .code(getUUIDCouponCode())
                    .assignedAt(nowDateLocal)
                    .expiredAt(getRandomExpiredAt(nowDateLocal))
                    .member(member)
                    .build();
        }

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder().couponId(couponResult.getId()).isUsed(false).build();

        couponInfoRepository.save(couponInfo);

        return couponResult;
    }

    // REPEATABLE_READ
    // SELECT 문장이 사용하는 모든 데이터에 shared lock이 걸리므로 다른 사용자는 그 영역에 해당되는 데이터에 대한 수정이 불가
    @LogExecutionTime
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String assignToUser(Member member) {
        // TODO: 트랜잭션 레벨 고려 (쿠폰을 멤버에 할당하는 도중, 다른 트랜잭션에서 이 쿠폰에 접근하거나 유저를 할당하면 안됨.)
        Coupon coupon = couponRepository.findByFreeUser();
        if(coupon == null) {
            coupon = save(member);
        } else {
            LocalDateTime assignedAt = LocalDateTime.now();

            coupon.setMember(member);  // update SQL
            coupon.setAssignedAt(assignedAt);
            coupon.setExpiredAt(getRandomExpiredAt(assignedAt));
        }
        return coupon.getCode();
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
        coupon.getCouponInfo().setUsed(isUsed);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findExpiredToday() {
        return couponRepository.findByExpiredToday();
    }

    @Transactional(readOnly = true)
    public List<Coupon> findByMember(long memberId) {
        return couponRepository.findByMemberId(memberId);
    }
}
