package com.example.mycoupon.domain.couponInfo;

import com.example.mycoupon.domain.coupon.Coupon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "coupon_info")
public class CouponInfo {

    @Id
    private long couponId;

    @JsonIgnore
    @OneToOne
    @JoinColumn
    @MapsId
    private Coupon coupon;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;  // 자주 값이 변경될 수 있는 컬럼이라 테이블을 분리함.

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_time")
    @UpdateTimestamp
    private Date lastUpdatedTime;   // 초기엔 null값 셋팅. isEnabled 필드값이 바뀌면 업데이트 됨.
}
