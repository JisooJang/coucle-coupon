package com.example.mycoupon.domain.coupon;

import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
        name = "coupon",
        indexes = {@Index(name = "coupon_expired_at_index", columnList = "expired_at", unique = false)}
)
public class Coupon {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "code", nullable = false, unique = true, updatable = false)
    private String code;  // TODO: 랜덤 코드, add index

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "expired_at", nullable = false, updatable = false)
    private Date expiredAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // FK

    // TODO : 연관관계의 주인은 Coupon entity
    // 대상테이블이 CouponInfo entity ( CouponInfo에는 Coupon entity 정보를 가지고 있을 필요가 없음)
    @OneToOne(mappedBy = "coupon")
    private CouponInfo couponInfo; // 1:1
}
