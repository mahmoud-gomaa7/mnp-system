package com.mnp.backend.service;

import com.mnp.backend.entity.PortingRequest;
import com.mnp.backend.enums.PortingRequestStatus;
import com.mnp.backend.repository.PortingRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PortingRequestCleanupService {

    private final PortingRequestRepository portingRequestRepository;
    private final long timeoutMinutes;

    public PortingRequestCleanupService(
            PortingRequestRepository portingRequestRepository,
            @Value("${mnp.porting-request.timeout-minutes:2}")
            long timeoutMinutes
    ) {
        if (timeoutMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Porting request timeout must be greater than zero"
            );
        }

        this.portingRequestRepository = portingRequestRepository;
        this.timeoutMinutes = timeoutMinutes;
    }

    @Scheduled(
            fixedRateString =
                    "${mnp.porting-request.cleanup-interval-ms:10000}"
    )
    @Transactional
    public void cancelExpiredRequests() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusMinutes(timeoutMinutes);

        List<PortingRequest> expiredRequests =
                portingRequestRepository.findByStatusAndCreatedAtBefore(
                        PortingRequestStatus.PENDING,
                        cutoff
                );

        for (PortingRequest request : expiredRequests) {
            request.cancel();
        }
    }
}
