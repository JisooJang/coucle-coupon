package com.example.mycoupon.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member implements Serializable {
    // 각 필드 validation 상세 annotation 추가할 것. (size, blank 등)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 30)
    @NotBlank
    @Column(name = "media_id", unique = true, nullable = false, length = 30)
    private String mediaId;  // TODO: add index

    @Size(min = 8, max = 100)
    @NotBlank
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Size(min = 10, max = 11)
    @NotBlank
    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Member(String mediaId, String password, String phoneNumber) {
        this.mediaId = mediaId;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
