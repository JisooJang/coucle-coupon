package com.example.mycoupon.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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

    @Size(min = 3, max = 30)
    @NotBlank
    @Column(name = "media_id", unique = true, nullable = false, length = 30)
    private String mediaId;  // TODO: add index

    @Size(min = 8, max = 100)
    @NotBlank
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Member(String mediaId, String password) {
        this.mediaId = mediaId;
        this.password = password;
    }
}
