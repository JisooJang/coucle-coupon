package com.example.mycoupon.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_info")
public class CouponInfo implements Serializable {
    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;  // 자주 값이 변경될 수 있는 컬럼이라 테이블을 분리함.

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Column(name = "last_updated_time")
    @UpdateTimestamp
    private LocalDateTime lastUpdatedTime;   // 초기엔 null값 셋팅. isEnabled 필드값이 바뀌면 업데이트 됨.
}
