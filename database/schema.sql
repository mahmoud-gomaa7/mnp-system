
CREATE TABLE operators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE phone_numbers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    number VARCHAR(11) NOT NULL UNIQUE,
    original_operator_id BIGINT NOT NULL,
    current_operator_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT fk_phone_original_operator
        FOREIGN KEY (original_operator_id) REFERENCES operators(id),

    CONSTRAINT fk_phone_current_operator
        FOREIGN KEY (current_operator_id) REFERENCES operators(id),

    CONSTRAINT chk_phone_status
        CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);


CREATE TABLE porting_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number_id BIGINT NOT NULL,
    donor_operator_id BIGINT NOT NULL,
    recipient_operator_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at TIMESTAMP NULL,

    CONSTRAINT fk_request_phone
        FOREIGN KEY (phone_number_id) REFERENCES phone_numbers(id),

    CONSTRAINT fk_request_donor
        FOREIGN KEY (donor_operator_id) REFERENCES operators(id),

    CONSTRAINT fk_request_recipient
        FOREIGN KEY (recipient_operator_id) REFERENCES operators(id),

    CONSTRAINT chk_request_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),

    CONSTRAINT chk_different_operators
        CHECK (donor_operator_id <> recipient_operator_id)
);

CREATE INDEX idx_request_phone_status
    ON porting_requests (phone_number_id, status);

CREATE INDEX idx_request_status_created
    ON porting_requests (status, created_at);


INSERT INTO operators (code, name)
VALUES
    ('vodafone', 'Vodafone'),
    ('orange', 'Orange'),
    ('etisalat', 'Etisalat');


INSERT INTO phone_numbers (
    number,
    original_operator_id,
    current_operator_id,
    status
)
SELECT '01012345678', id, id, 'ACTIVE'
FROM operators
WHERE code = 'vodafone';

INSERT INTO phone_numbers (
    number,
    original_operator_id,
    current_operator_id,
    status
)
SELECT '01212345678', id, id, 'ACTIVE'
FROM operators
WHERE code = 'orange';

INSERT INTO phone_numbers (
    number,
    original_operator_id,
    current_operator_id,
    status
)
SELECT '01112345678', id, id, 'ACTIVE'
FROM operators
WHERE code = 'etisalat';
