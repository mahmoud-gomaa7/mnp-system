package com.mnp.backend.repository;

import com.mnp.backend.entity.PortingRequest;
import com.mnp.backend.enums.PortingRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PortingRequestRepository
        extends JpaRepository<PortingRequest, Long> {

    boolean existsByPhoneNumber_NumberAndStatus(
            String number,
            PortingRequestStatus status
    );

    List<PortingRequest> findByStatusAndCreatedAtBefore(
            PortingRequestStatus status,
            LocalDateTime cutoff
    );


    @EntityGraph(attributePaths = {
            "phoneNumber",
            "donorOperator",
            "recipientOperator"
    })
    @Query("SELECT r FROM PortingRequest r WHERE r.id = :id")
    Optional<PortingRequest> findByIdWithDetails(
            @Param("id") Long id
    );



    @Query(
            value = """
        SELECT r FROM PortingRequest r
        WHERE r.donorOperator.code = :code
           OR r.recipientOperator.code = :code
           OR r.status = :acceptedStatus
        ORDER BY r.createdAt DESC, r.id DESC
        """,
            countQuery = """
        SELECT COUNT(r) FROM PortingRequest r
        WHERE r.donorOperator.code = :code
           OR r.recipientOperator.code = :code
           OR r.status = :acceptedStatus
        """
    )
    @EntityGraph(attributePaths = {
            "phoneNumber",
            "donorOperator",
            "recipientOperator"
    })
    Page<PortingRequest> findVisibleRequests(
            @Param("code") String code,
            @Param("acceptedStatus") PortingRequestStatus acceptedStatus,
            Pageable pageable
    );



}
