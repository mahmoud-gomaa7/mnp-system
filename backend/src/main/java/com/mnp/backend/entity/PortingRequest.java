package com.mnp.backend.entity;

import com.mnp.backend.enums.PortingRequestStatus;
import com.mnp.backend.exception.RequestConflictException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
@Table(name = "porting_requests")
public class PortingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phone_number_id", nullable = false)
    private PhoneNumber phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donor_operator_id", nullable = false)
    private Operator donorOperator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_operator_id", nullable = false)
    private Operator recipientOperator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PortingRequestStatus status = PortingRequestStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime decidedAt;

    public PortingRequest(
            PhoneNumber phoneNumber,
            Operator donorOperator,
            Operator recipientOperator
    ) {
        this.phoneNumber = phoneNumber;
        this.donorOperator = donorOperator;
        this.recipientOperator = recipientOperator;
        this.status = PortingRequestStatus.PENDING;
    }

    protected PortingRequest(){

    }

    public Long getId() {
        return id;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Operator getDonorOperator() {
        return donorOperator;
    }

    public Operator getRecipientOperator() {
        return recipientOperator;
    }

    public PortingRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    public void accept() {
        ensurePending();
        this.status = PortingRequestStatus.ACCEPTED;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject() {
        ensurePending();
        this.status = PortingRequestStatus.REJECTED;
        this.decidedAt = LocalDateTime.now();
    }

    public void cancel() {
        ensurePending();
        this.status = PortingRequestStatus.CANCELLED;
        this.decidedAt = LocalDateTime.now();
    }

    private void ensurePending() {
        if (this.status != PortingRequestStatus.PENDING) {
            throw new RequestConflictException(
                    "Only pending requests can be decided"
            );
        }
    }

}
