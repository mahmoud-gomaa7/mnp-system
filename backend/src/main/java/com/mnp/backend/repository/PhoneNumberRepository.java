package com.mnp.backend.repository;

import com.mnp.backend.entity.PhoneNumber;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhoneNumberRepository
        extends JpaRepository<PhoneNumber, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PhoneNumber p WHERE p.number = :number")
    Optional<PhoneNumber> findByNumberForUpdate(
            @Param("number") String number
    );

    @EntityGraph(attributePaths = {"currentOperator"})
    @Query("""
        SELECT p FROM PhoneNumber p
        WHERE p.number = :number
        """)
    Optional<PhoneNumber> findByNumberWithCurrentOperator(
            @Param("number") String number
    );

}
