package com.example.mycoupon.domain.member;

import com.example.mycoupon.common.ValidationRegex;
import com.example.mycoupon.domain.coupon.Coupon;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(name = "media_id", unique = true, nullable = false, length = 30)
    private String mediaId;  // TODO: add index

    @NotBlank
    @Pattern(regexp = ValidationRegex.PASSWORD)
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

// 회원은 쿠폰을 갖고 있을 수도 있고, 안갖고 있을 수도 있다.
    @OneToMany // LAZY init
    @JoinColumn(name = "member_id") // Coupon 테이블의 member_id (FK)
    private List<Coupon> coupons;

    @Builder
    public Member(String mediaId, String password) {
        this.mediaId = mediaId;
        this.password = password;
    }
}
