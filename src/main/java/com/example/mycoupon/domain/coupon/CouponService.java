package com.example.mycoupon.domain.coupon;

import com.example.mycoupon.common.ValidationRegex;
import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.exceptions.CouponMemberNotMatchException;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import com.example.mycoupon.exceptions.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    private final Calendar calendar;

    //private final EntityManager entityManager;

    @Autowired
    public CouponService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository,
                         Calendar calendar) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
        this.calendar = calendar;
    }

    public void validateCouponCode(String code) throws IllegalArgumentException {
        if(!Pattern.matches(ValidationRegex.COUPONCODE, code)) {
            throw new IllegalArgumentException("Invalid format of coupon code.");
        }
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
        for(int i=0 ; i<n ; i++) {
            save(null);
        }
    }

    // TODO: 트랜잭션 격리 레벨 설정
    @Transactional
    public Coupon save(Member member) {
        Coupon coupon;
        Date nowDate = new Date();
        if(member == null) {
            coupon = Coupon.builder()
                    .code(getUUIDCouponCode())
                    .build();
        } else {
            coupon = Coupon.builder()
                    .code(getUUIDCouponCode())
                    .assignedAt(nowDate)
                    .expiredAt(getRandomExpiredAt(nowDate))
                    .member(member)
                    .build();
        }

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder().coupon(couponResult).isUsed(false).build();
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
            Date assignedAt = new Date();
            coupon.setMember(member);  // update SQL
            coupon.setAssignedAt(assignedAt);
            coupon.setExpiredAt(getRandomExpiredAt(assignedAt));
            //TODO: assignedAt, expiredAt 변경 감지가 적용 안됨. member는 변경 감지 적용하여 update 쿼리 실행
        }
        return coupon.getCode();
    }

    @Transactional
    public void updateIsEnabledCouponById(String code, long memberId, boolean isUsed) throws CouponNotFoundException {
        // TODO : transaction 레벨 설정
        Coupon coupon = couponRepository.findByCode(code);
        if(coupon == null) {
            throw new CouponNotFoundException(code);
        }
        if(coupon.getMember().getId() != memberId) {
            throw new CouponMemberNotMatchException(code);
        }
        coupon.getCouponInfo().setUsed(isUsed);
    }

    public List<Coupon> findExpiredToday() {
        return couponRepository.findByExpiredToday();
    }

    public List<Coupon> findByMember(long memberId) {
        return couponRepository.findByMemberId(memberId);
    }
}
