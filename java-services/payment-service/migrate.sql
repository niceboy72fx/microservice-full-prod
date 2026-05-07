CREATE USER payment_db IDENTIFIED BY "123456";

GRANT CONNECT, RESOURCE TO payment_db;

ALTER USER payment_db QUOTA UNLIMITED ON USERS;

ALTER SESSION SET CURRENT_SCHEMA = payment_db;

CREATE TABLE payments (
  id VARCHAR2(64) PRIMARY KEY,
  user_id VARCHAR2(64) NOT NULL,
  type VARCHAR2(20) NOT NULL,
  amount NUMBER(18,2) NOT NULL,
  currency VARCHAR2(10) NOT NULL,
  method VARCHAR2(50) NOT NULL,
  status VARCHAR2(20) NOT NULL,
  provider VARCHAR2(50) NOT NULL,
  provider_transaction_id VARCHAR2(100),
  idempotency_key VARCHAR2(100) NOT NULL,
  correlation_id VARCHAR2(100) NOT NULL,
  failure_reason VARCHAR2(1000),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX uk_payments_user_idempotency ON payments(user_id, idempotency_key);
CREATE INDEX idx_payments_correlation_id ON payments(correlation_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TABLE payment_status_history (
  id VARCHAR2(64) PRIMARY KEY,
  payment_id VARCHAR2(64) NOT NULL,
  from_status VARCHAR2(20),
  to_status VARCHAR2(20) NOT NULL,
  reason VARCHAR2(1000),
  created_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_payment_status_history_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE INDEX idx_payment_status_history_payment_id ON payment_status_history(payment_id);

COMMIT;
