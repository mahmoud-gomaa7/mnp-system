package com.mnp.backend.controller;

import com.mnp.backend.dto.CreatePortingRequestDto;
import com.mnp.backend.dto.PortingRequestResponseDto;
import com.mnp.backend.entity.PortingRequest;
import com.mnp.backend.service.PortingRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/porting-requests")
public class PortingRequestController {

    private final PortingRequestService portingRequestService;

    public PortingRequestController(
            PortingRequestService portingRequestService
    ) {
        this.portingRequestService = portingRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortingRequestResponseDto createRequest(
            @RequestHeader("organization") String organization,
            @Valid @RequestBody CreatePortingRequestDto requestDto
    ) {
        PortingRequest request =
                portingRequestService.createRequest(
                        requestDto.phoneNumber(),
                        organization
                );

        return PortingRequestResponseDto.from(request);
    }


    @PostMapping("/{id}/accept")
    public PortingRequestResponseDto acceptRequest(
            @RequestHeader("organization") String organization,
            @PathVariable Long id
    ) {
        PortingRequest request = portingRequestService.decideRequest(
                id,
                organization,
                true
        );

        return PortingRequestResponseDto.from(request);
    }

    @PostMapping("/{id}/reject")
    public PortingRequestResponseDto rejectRequest(
            @RequestHeader("organization") String organization,
            @PathVariable Long id
    ) {
        PortingRequest request = portingRequestService.decideRequest(
                id,
                organization,
                false
        );

        return PortingRequestResponseDto.from(request);
    }



    @GetMapping
    public Page<PortingRequestResponseDto> getRequests(
            @RequestHeader("organization") String organization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page index must not be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        return portingRequestService
                .getVisibleRequests(organization, page, size)
                .map(PortingRequestResponseDto::from);
    }



}
