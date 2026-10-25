package com.mnp.backend.service;

import com.mnp.backend.entity.Operator;
import com.mnp.backend.entity.PhoneNumber;
import com.mnp.backend.repository.PhoneNumberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {

    private final OperatorService operatorService;
    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumberService(
            OperatorService operatorService,
            PhoneNumberRepository phoneNumberRepository
    ) {
        this.operatorService = operatorService;
        this.phoneNumberRepository = phoneNumberRepository;
    }

    public void validatePhoneNumber(String number) {
        if (number == null || !number.matches("01[012][0-9]{8}")) {
            throw new IllegalArgumentException(
                    "Phone number must be an 11-digit number "
                            + "starting with 010, 011, or 012"
            );
        }
    }

    public Operator getInitialOperator(String number) {
        validatePhoneNumber(number);

        String operatorCode = switch (number.substring(0, 3)) {
            case "010" -> "vodafone";
            case "012" -> "orange";
            case "011" -> "etisalat";
            default -> throw new IllegalArgumentException(
                    "Unsupported phone number prefix"
            );
        };

        return operatorService.getOperatorByCode(operatorCode);
    }

    public PhoneNumber getPhoneNumberByNumber(
            String number,
            String organization
    ) {
        // Validate the requesting operator.
        operatorService.getOperatorByCode(organization);

        // Validate the phone number format.
        validatePhoneNumber(number);

        // Find the number and fetch its current operator.
        return phoneNumberRepository
                .findByNumberWithCurrentOperator(number)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Phone number not found: " + number
                ));
    }
}
