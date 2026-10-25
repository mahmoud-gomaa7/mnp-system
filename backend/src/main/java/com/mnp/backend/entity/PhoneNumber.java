
package com.mnp.backend.entity;

import com.mnp.backend.enums.PhoneNumberStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "phone_numbers")
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "original_operator_id", nullable = false)
    private Operator originalOperator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_operator_id", nullable = false)
    private Operator currentOperator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneNumberStatus status = PhoneNumberStatus.ACTIVE;

    public PhoneNumber(
            String number,
            Operator originalOperator
    ) {
        this.number = number;
        this.originalOperator = originalOperator;
        this.currentOperator = originalOperator;
        this.status = PhoneNumberStatus.ACTIVE;
    }
    protected PhoneNumber(){

    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Operator getOriginalOperator() {
        return originalOperator;
    }

    public Operator getCurrentOperator() {
        return currentOperator;
    }

    public PhoneNumberStatus getStatus() {
        return status;
    }

    public void changeCurrentOperator(Operator newOperator) {
        if (newOperator == null) {
            throw new IllegalArgumentException(
                    "New operator is required"
            );
        }

        this.currentOperator = newOperator;
    }
}
