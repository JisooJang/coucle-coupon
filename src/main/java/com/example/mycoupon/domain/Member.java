package com.example.mycoupon.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(name = "member_id", unique = true, nullable = false, length = 30)
    private String memberId;  // add index

    @NotBlank
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

//    @OneToOne
//    @JoinColumn()
//    private Coupon coupon;

    @Builder
    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
    }
}
