package com.mnp.backend.controller;

import com.mnp.backend.dto.PhoneNumberResponseDto;
import com.mnp.backend.entity.PhoneNumber;
import com.mnp.backend.service.PhoneNumberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/phone-numbers")
public class PhoneNumberController {

    private final PhoneNumberService phoneNumberService;

    public PhoneNumberController(
            PhoneNumberService phoneNumberService
    ) {
        this.phoneNumberService = phoneNumberService;
    }

    @GetMapping("/{number}")
    public PhoneNumberResponseDto getPhoneNumber(
            @RequestHeader("organization") String organization,
            @PathVariable String number
    ) {
        PhoneNumber phoneNumber =
                phoneNumberService.getPhoneNumberByNumber(
                        number,
                        organization
                );

        return PhoneNumberResponseDto.from(phoneNumber);
    }
}
