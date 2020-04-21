package com.example.mycoupon.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "coupon_info")
public class CouponInfo {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    private long couponId;

    @OneToOne
    @JoinColumn
    @MapsId
    private Coupon coupon;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;
}
