package com.example.mycoupon.coupon;

import com.example.mycoupon.domain.coupon.Coupon;
import com.example.mycoupon.domain.coupon.CouponRepository;
import com.example.mycoupon.domain.couponInfo.CouponInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CouponRepositoryTest {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Before
    public void prepareData() throws Exception {
        // insert dummy data.
        for(int i=0 ; i<10 ; i++) {
            Coupon coupon = Coupon.builder()
                    .code("code" + i)
                    .createdAt(new Date())
                    .build();
            coupon = this.entityManager.persist(coupon);

            CouponInfo couponInfo = CouponInfo.builder()
                    .coupon(coupon)
                    .isEnabled(true)
                    .build();
            couponInfo = this.entityManager.persist(couponInfo);
        }
    }
    @Test
    public void findByFreeUsers() throws Exception {
        Coupon freeCoupon = couponRepository.findByFreeUser();
        assertThat(freeCoupon.getCode()).isEqualTo("code1");
    }

    @Test
    public void findByCode() throws Exception {

    }

    @Test
    public void findByMemberId() throws Exception {

    }

    @Test
    public void findByExpiredToday() throws Exception {

    }
}
