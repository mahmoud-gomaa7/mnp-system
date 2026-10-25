package com.mnp.backend.service;

import com.mnp.backend.entity.Operator;
import com.mnp.backend.entity.PhoneNumber;
import com.mnp.backend.entity.PortingRequest;
import com.mnp.backend.enums.PortingRequestStatus;
import com.mnp.backend.exception.DuplicatePendingRequestException;
import com.mnp.backend.exception.ForbiddenOperationException;
import com.mnp.backend.repository.PhoneNumberRepository;
import com.mnp.backend.repository.PortingRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PortingRequestService {

    private final OperatorService operatorService;
    private final PhoneNumberService phoneNumberService;
    private final PhoneNumberRepository phoneNumberRepository;
    private final PortingRequestRepository portingRequestRepository;

    public PortingRequestService(
            OperatorService operatorService,
            PhoneNumberService phoneNumberService,
            PhoneNumberRepository phoneNumberRepository,
            PortingRequestRepository portingRequestRepository
    ) {
        this.operatorService = operatorService;
        this.phoneNumberService = phoneNumberService;
        this.phoneNumberRepository = phoneNumberRepository;
        this.portingRequestRepository = portingRequestRepository;
    }

    @Transactional
    public PortingRequest createRequest(
            String phoneNumberValue,
            String recipientOrganization
    ) {
        phoneNumberService.validatePhoneNumber(phoneNumberValue);

        Operator recipient = operatorService.getOperatorByCode(
                recipientOrganization
        );

        PhoneNumber phoneNumber = phoneNumberRepository
                .findByNumberForUpdate(phoneNumberValue)
                .orElseGet(() -> {
                    Operator initialOperator =
                            phoneNumberService.getInitialOperator(
                                    phoneNumberValue
                            );

                    return phoneNumberRepository.save(
                            new PhoneNumber(
                                    phoneNumberValue,
                                    initialOperator
                            )
                    );
                });

        Operator donor = phoneNumber.getCurrentOperator();

        if (donor.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException(
                    "The recipient must differ from the current operator"
            );
        }

        boolean pendingExists =
                portingRequestRepository
                        .existsByPhoneNumber_NumberAndStatus(
                                phoneNumberValue,
                                PortingRequestStatus.PENDING
                        );

        if (pendingExists) {
            throw new DuplicatePendingRequestException(
                    "A pending porting request already exists for this number"
            );
        }

        PortingRequest request = new PortingRequest(
                phoneNumber,
                donor,
                recipient
        );

        PortingRequest savedRequest =
                portingRequestRepository.save(request);

        return portingRequestRepository
                .findByIdWithDetails(savedRequest.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Created porting request could not be retrieved"
                ));
    }

    @Transactional
    public PortingRequest decideRequest(
            Long requestId,
            String organization,
            boolean accept
    ) {
        Operator actingOperator =
                operatorService.getOperatorByCode(organization);

        PortingRequest request = portingRequestRepository
                .findByIdWithDetails(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Porting request not found: " + requestId
                ));

        if (!request.getDonorOperator().getId()
                .equals(actingOperator.getId())) {
            throw new ForbiddenOperationException(
                    "Only the donor operator can decide this request"
            );
        }

        if (accept) {
            request.accept();
            request.getPhoneNumber().changeCurrentOperator(
                    request.getRecipientOperator()
            );
        } else {
            request.reject();
        }

        return request;
    }



    @Transactional(readOnly = true)
    public Page<PortingRequest> getVisibleRequests(
            String organization,
            int page,
            int size
    ) {
        Operator operator = operatorService.getOperatorByCode(organization);

        Pageable pageable = PageRequest.of(page, size);

        return portingRequestRepository.findVisibleRequests(
                operator.getCode(),
                PortingRequestStatus.ACCEPTED,
                pageable
        );
    }

}
