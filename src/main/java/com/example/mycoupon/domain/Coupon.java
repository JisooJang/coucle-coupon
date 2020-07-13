package com.example.mycoupon.domain;

import com.example.mycoupon.utils.ValidationRegex;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "coupon",
        indexes = {@Index(name = "coupon_expired_at_index", columnList = "expired_at", unique = false)}
)
public class Coupon implements Serializable {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Pattern(regexp = ValidationRegex.COUPONCODE)
    @Column(name = "code", nullable = false, unique = true, updatable = false)
    private String code;  // TODO: 랜덤 코드, add index

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // FK

    @OneToOne
    @PrimaryKeyJoinColumn
    private CouponInfo couponInfo;

    // 낙관적 락 사용(여러 트랜잭션에서 유저에게 할당할 때 대비 => 최초 커밋만 인정정)
    @Version
    private Integer version;
}
