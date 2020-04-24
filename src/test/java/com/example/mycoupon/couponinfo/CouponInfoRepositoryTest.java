package com.example.mycoupon.couponinfo;

import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class CouponInfoRepositoryTest {
    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private TestEntityManager entityManager;
}
