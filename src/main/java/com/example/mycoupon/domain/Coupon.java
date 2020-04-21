package com.example.mycoupon.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;  // 랜덤 코드, add index

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Column(name = "expired_at", nullable = false, updatable = false)
    private Date expiredAt;

    @Column(name = "user_id")   // 1:1
    private long userId; // FK

    @OneToOne(mappedBy = "coupon")
    private CouponInfo couponInfo;
}
