package com.example.mycoupon.member;

import com.example.mycoupon.domain.member.Member;
import com.example.mycoupon.domain.member.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void findByMediaId() {
        String testMediaId = "test1234";
        Member m = Member.builder()
                .mediaId(testMediaId)
                .password(passwordEncoder.encode("qwer1234!"))
                .build();

        m = entityManager.persist(m);

        Member result = memberRepository.findByMediaId(testMediaId);

        assertThat(result.getMediaId()).isEqualTo(testMediaId);
    }

    @Test
    public void findById() {
        String testMediaId = "test1234";
        Member m = Member.builder()
                .mediaId(testMediaId)
                .password(passwordEncoder.encode("qwer1234!"))
                .build();

        m = entityManager.persist(m);

        Optional<Member> result = memberRepository.findById(m.getId());

        assertThat(result.isPresent()).isEqualTo(true);
        assertThat(result.get().getId()).isEqualTo(m.getId());
    }

    @Test
    public void save() {
        String testMediaId = "test1234";
        Member m = Member.builder()
                .mediaId(testMediaId)
                .password(passwordEncoder.encode("qwer1234!"))
                .build();

        Member result = memberRepository.save(m);

        assertThat(entityManager.find(Member.class, m.getId())).isEqualTo(result);

    }

    @Test(expected = ConstraintViolationException.class)
    public void savePasswordValidationFailed() {
        String testMediaId = "test1234";
        Member m = Member.builder()
                .mediaId(testMediaId)
                .password(passwordEncoder.encode("qwer1234"))
                .build();

        Member result = memberRepository.save(m);
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveIdValidationFailed() {
        String testMediaId = " ";
        Member m = Member.builder()
                .mediaId(testMediaId)
                .password(passwordEncoder.encode("qwer1234!"))
                .build();

        Member result = memberRepository.save(m);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveMemberIdAlreadyExistsFailed() {
        for(int i=0 ; i<2 ; i++) {
            String testMediaId = "test1234";
            Member m = Member.builder()
                    .mediaId(testMediaId)
                    .password(passwordEncoder.encode("qwer1234!"))
                    .build();

            Member result = memberRepository.save(m);
        }
    }
}
