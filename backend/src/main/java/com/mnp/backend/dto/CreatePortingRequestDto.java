package com.mnp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePortingRequestDto(

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "01[012][0-9]{8}",
                message = "Phone number must be 11 digits and start with 010, 011, or 012"
        )
        String phoneNumber

) {
}
