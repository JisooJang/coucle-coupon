package com.example.mycoupon.domain.coupon;

import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    private final Calendar calendar;

    @Autowired
    public CouponService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository, Calendar calendar) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
        this.calendar = calendar;
    }

    public String getUUIDCouponCode() {
        // format : uuid
        return UUID.randomUUID().toString();
    }

    public Date getRandomExpiredAt(Date fromDate) {
        // add random expired days from now date (1 day ~ 7 days)
        calendar.setTime(fromDate);
        calendar.add(Calendar.DATE, (int)(Math.random() * 7) + 1);
        return calendar.getTime();
    }


    public void bulkSave(int n) {
        // TODO: save() n번 반복? 멀티 쓰레딩?
        save(null);
    }

    // TODO: 트랜잭션 격리 레벨 설정
    @Transactional
    public Coupon save(Member member) {
        Date createdAt = new Date();
        Coupon coupon = Coupon.builder()
                .code(getUUIDCouponCode())
                .createdAt(createdAt)
                .expiredAt(getRandomExpiredAt(createdAt))
                .member(member)
                .build();

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder().coupon(couponResult).isEnabled(true).build();
        couponInfoRepository.save(couponInfo);

        return couponResult;
    }

    @Transactional
    public String assignToUser(Member member) {
        // TODO: 트랜잭션 레벨 고려 (쿠폰을 멤버에 할당하는 도중, 다른 트랜잭션에서 이 쿠폰에 접근하거나 유저를 할당하면 안됨.)
        Coupon coupon = couponRepository.findByFreeUser();
        if(coupon == null) {
            coupon = save(member);
        } else {
            coupon.setMember(member);  // update SQL
        }
        return coupon.getCode();
    }

    @Transactional
    public void updateIsEnabledCouponById(String code, long memberId, boolean isEnabled) throws CouponNotFoundException {
        // TODO : transaction 레벨 설정
        //couponInfoRepository.updateByCode(code, isEnabled);
        Coupon coupon = couponRepository.findByCode(code);
        if(coupon == null) {
            throw new CouponNotFoundException(code);
        }
        if(coupon.getMember().getId() != memberId) {
            throw new CouponMemberNotMatchException(code);
        }
        coupon.getCouponInfo().setEnabled(isEnabled);
    }

    public List<Coupon> findExpiredToday() {
        return couponRepository.findByExpiredToday();
    }

    public List<Coupon> findByMember(long memberId) {
        return couponRepository.findByMemberId(memberId);
    }
}
