package com.example.mycoupon.couponinfo;

import com.example.mycoupon.domain.coupon.Coupon;
import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CouponInfoRepositoryTest {
    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void save() {
        Coupon coupon = Coupon.builder()
                .code(UUID.randomUUID().toString())
                .build();

        coupon = entityManager.persist(coupon);

        CouponInfo info = CouponInfo.builder()
                .couponId(coupon.getId())
                .isUsed(false)
                .build();
        CouponInfo result = couponInfoRepository.save(info);

        assertThat(entityManager.find(CouponInfo.class, info.getCouponId())).isEqualTo(result);
    }
}
