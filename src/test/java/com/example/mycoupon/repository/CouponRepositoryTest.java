package com.example.mycoupon.repository;

import com.example.mycoupon.domain.Coupon;
import com.example.mycoupon.domain.CouponInfo;
import com.example.mycoupon.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CouponRepositoryTest {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void findByFreeUsers() throws Exception {
        Coupon coupon = Coupon.builder()
                .code(UUID.randomUUID().toString())
                .build();
        coupon = this.entityManager.persist(coupon);

        CouponInfo couponInfo = CouponInfo.builder()
                .couponId(coupon.getId())
                .isUsed(false)
                .build();
        couponInfo = this.entityManager.persist(couponInfo);

        Coupon freeCoupon = couponRepository.findByFreeUser();
        assertThat(freeCoupon).isNotNull();
        assertThat(freeCoupon.getMember()).isNull();
        assertThat(freeCoupon.getAssignedAt()).isNull();
        assertThat(freeCoupon.getExpiredAt()).isNull();
    }

    @Test
    public void findByCode() throws Exception {
        String code = UUID.randomUUID().toString();
        Coupon coupon = Coupon.builder()
                .code(code)
                .build();
        coupon = this.entityManager.persist(coupon);

        Coupon result = couponRepository.findByCode(code);
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(code);
    }

    @Test
    public void findByMemberId() throws Exception {
        Member member = Member.builder()
                .mediaId("test1")
                .password(passwordEncoder.encode("qwer1234!"))
                .build();
        member = this.entityManager.persist(member);

        for(int i=0 ; i<2 ; i++) {
            Coupon coupon = Coupon.builder()
                    .code(UUID.randomUUID().toString())
                    .member(member)
                    .build();
            coupon = this.entityManager.persist(coupon);

            CouponInfo couponInfo = CouponInfo.builder()
                    .couponId(coupon.getId())
                    .isUsed(false)
                    .build();
            couponInfo = this.entityManager.persist(couponInfo);
        }


        List<Coupon> results = couponRepository.findByMemberId(member.getId());
        assertThat(results).size().isEqualTo(2);
        assertThat(results.get(0).getMember().getId()).isEqualTo(member.getId());
        assertThat(results.get(1).getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void findByExpiredToday() throws Exception {
        Coupon coupon = Coupon.builder()
                .code(UUID.randomUUID().toString())
                .expiredAt(new Date())
                .build();
        coupon = this.entityManager.persist(coupon);

        List<Coupon> results = couponRepository.findByExpiredToday();
        assertThat(results).size().isEqualTo(1);
    }

    @Test
    public void save() throws Exception {
        String code = UUID.randomUUID().toString();
        Coupon coupon = Coupon.builder()
                .code(code)
                .build();

        Coupon result = couponRepository.save(coupon);
        assertThat(result).isEqualTo(coupon);
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveInvalidUUIDCode() throws Exception {
        Coupon coupon = Coupon.builder()
                .code("test123")
                .build();
        Coupon result = couponRepository.save(coupon);
    }
}
