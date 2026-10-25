package com.mnp.backend.service;

import com.mnp.backend.entity.Operator;
import com.mnp.backend.repository.OperatorRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class OperatorService {

    private final OperatorRepository operatorRepository;

    public OperatorService(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public Operator getOperatorByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization header is required"
            );
        }

        String normalizedCode = code.trim().toLowerCase(Locale.ROOT);

        return operatorRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown operator: " + normalizedCode
                ));
    }
}
