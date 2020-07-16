package com.example.mycoupon.template;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmTalk {
    private String countryCode;
    private String phoneNumber;
    private String message;

    @Builder
    public AlarmTalk(String countryCode, String phoneNumber, String message) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.message = message;
    }
}
