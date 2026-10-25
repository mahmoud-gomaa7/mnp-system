package com.mnp.backend.dto;

import com.mnp.backend.entity.PortingRequest;
import com.mnp.backend.enums.PortingRequestStatus;

import java.time.LocalDateTime;

public record PortingRequestResponseDto(
        Long id,
        String phoneNumber,
        String donorOperator,
        String recipientOperator,
        PortingRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime decidedAt
) {

    public static PortingRequestResponseDto from(
            PortingRequest request
    ) {
        return new PortingRequestResponseDto(
                request.getId(),
                request.getPhoneNumber().getNumber(),
                request.getDonorOperator().getCode(),
                request.getRecipientOperator().getCode(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getDecidedAt()
        );
    }
}
