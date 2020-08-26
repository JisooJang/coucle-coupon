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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

        Optional<Coupon> freeCoupon = couponRepository.findByFreeUser();
        assertThat(freeCoupon.isPresent()).isNotNull();
        assertThat(freeCoupon.get()).isEqualTo(coupon);
        assertThat(freeCoupon.get().getMember()).isNull();
        assertThat(freeCoupon.get().getAssignedAt()).isNull();
        assertThat(freeCoupon.get().getExpiredAt()).isNull();
    }

    @Test
    public void findByCode() throws Exception {
        String code = UUID.randomUUID().toString();
        Coupon coupon = Coupon.builder()
                .code(code)
                .build();
        coupon = this.entityManager.persist(coupon);

        Optional<Coupon> result = couponRepository.findByCode(code);
        assertThat(result.isPresent()).isEqualTo(true);
        assertThat(result.get().getCode()).isEqualTo(code);
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
                .expiredAt(LocalDateTime.now())
                .build();
        coupon = this.entityManager.persist(coupon);

        Optional<List<Coupon>> results = couponRepository.findByExpiredToday();
        assertThat(results.isPresent()).isEqualTo(true);
        assertThat(results.get()).size().isEqualTo(1);
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

    @Test
    public void findByExpiredAfter3Days() throws Exception {
        for(int i=0 ; i<2 ; i++) {
            Coupon coupon = Coupon.builder()
                    .code(UUID.randomUUID().toString())
                    .expiredAt(LocalDateTime.now().plusDays(3))
                    .build();
            this.entityManager.persist(coupon);
        }

        List<Coupon> result = couponRepository.findByExpiredAfter3Days();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void findByExpiredAfter3DaysNone() throws Exception {
        for(int i=0 ; i<2 ; i++) {
            Coupon coupon = Coupon.builder()
                    .code(UUID.randomUUID().toString())
                    .expiredAt(LocalDateTime.now())
                    .build();
            this.entityManager.persist(coupon);
        }
        List<Coupon> result = couponRepository.findByExpiredAfter3Days();
        assertThat(result.size()).isEqualTo(0);
    }
}
