package com.example.mycoupon.domain;

import com.example.mycoupon.utils.ValidationRegex;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
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

    @Pattern(regexp = ValidationRegex.COUPONCODE)
    @Column(name = "code", nullable = false, unique = true, updatable = false)
    private String code;  // TODO: 랜덤 코드, add index

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "expired_at")
    private Date expiredAt;

    @Column(name = "assigned_at")
    private Date assignedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // FK

    @OneToOne
    @PrimaryKeyJoinColumn
    private CouponInfo couponInfo;
}
