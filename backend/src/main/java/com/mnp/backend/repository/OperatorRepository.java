package com.mnp.backend.repository;

import com.mnp.backend.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorRepository
        extends JpaRepository<Operator,Long> {

    Optional<Operator> findByCode(String code);
}
