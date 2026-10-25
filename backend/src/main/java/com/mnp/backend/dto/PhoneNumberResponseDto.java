package com.mnp.backend.dto;

import com.mnp.backend.entity.PhoneNumber;
import com.mnp.backend.enums.PhoneNumberStatus;

public record PhoneNumberResponseDto(
        String phoneNumber,
        PhoneNumberStatus status,
        String currentOperator
) {
    public static PhoneNumberResponseDto from(PhoneNumber phoneNumber) {
        return new PhoneNumberResponseDto(
                phoneNumber.getNumber(),
                phoneNumber.getStatus(),
                phoneNumber.getCurrentOperator().getCode()
        );
    }
}
