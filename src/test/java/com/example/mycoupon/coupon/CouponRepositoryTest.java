package com.example.mycoupon.coupon;

import com.example.mycoupon.domain.coupon.Coupon;
import com.example.mycoupon.domain.coupon.CouponRepository;
import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.domain.member.Member;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
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
                    .isUsed(false)
                    .build();
            couponInfo = this.entityManager.persist(couponInfo);
        }

        Member member = Member.builder().mediaId("test1").password("qwerQQ1234!!").build();
        member = this.entityManager.persist(member);

        Coupon coupon1 = this.entityManager.find(Coupon.class, 1L);
        Coupon coupon2 = this.entityManager.find(Coupon.class, 2L);

        Date nowDate = new Date();
        coupon1.setMember(member);
        coupon1.setAssignedAt(nowDate);
        coupon1.setExpiredAt(nowDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        cal.add(Calendar.DATE, 2);

        coupon2.setMember(member);
        coupon2.setAssignedAt(nowDate);
        coupon2.setExpiredAt(cal.getTime());

        this.entityManager.flush();  // UPDATE DB 반영
    }

    @Test
    public void findByFreeUsers() throws Exception {
        Coupon freeCoupon = couponRepository.findByFreeUser();
        assertThat(freeCoupon).isNotNull();
        assertThat(freeCoupon.getMember()).isNull();
        assertThat(freeCoupon.getAssignedAt()).isNull();
        assertThat(freeCoupon.getExpiredAt()).isNull();
    }

    @Test
    public void findByCode() throws Exception {
        String findCode = "code1";
        Coupon coupon = couponRepository.findByCode(findCode);
        assertThat(coupon).isNotNull();
        assertThat(coupon.getCode()).isEqualTo(findCode);
    }

    @Test
    public void findByMemberId() throws Exception {
        List<Coupon> results = couponRepository.findByMemberId(1);
        assertThat(results).size().isEqualTo(2);
        assertThat(results.get(0).getMember().getId()).isEqualTo(1);
        assertThat(results.get(1).getMember().getId()).isEqualTo(1);
    }

    @Test
    public void findByExpiredToday() throws Exception {
        List<Coupon> results = couponRepository.findByExpiredToday();
        assertThat(results).size().isEqualTo(1);
        assertThat(results.get(0).getMember()).isNotNull();
        assertThat(results.get(0).getAssignedAt()).isNotNull();
    }

    @Test
    public void save() throws Exception {
        Coupon coupon = Coupon.builder()
                .code(UUID.randomUUID().toString())
                .createdAt(new Date())
                .build();
        Coupon result = couponRepository.save(coupon);

        assertThat(result).isEqualTo(coupon);
    }
}
