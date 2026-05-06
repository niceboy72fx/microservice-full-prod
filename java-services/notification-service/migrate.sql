CREATE USER notification_db IDENTIFIED BY "123456";

GRANT CONNECT, RESOURCE TO notification_db;

ALTER USER notification_db QUOTA UNLIMITED ON USERS;

ALTER SESSION SET CURRENT_SCHEMA = notification_db;

CREATE TABLE notification_processed_event (
  event_id VARCHAR2(100) PRIMARY KEY,
  correlation_id VARCHAR2(100),
  processed_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

CREATE TABLE notification_delivery_log (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  event_id VARCHAR2(100) NOT NULL,
  correlation_id VARCHAR2(100),
  user_id VARCHAR2(100),
  recipient VARCHAR2(255) NOT NULL,
  channel VARCHAR2(30) DEFAULT 'EMAIL' NOT NULL,
  subject VARCHAR2(500),
  status VARCHAR2(30) NOT NULL,
  retry_count NUMBER(5) DEFAULT 0 NOT NULL,
  failure_reason VARCHAR2(1000),
  source_topic VARCHAR2(255),
  create_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
  update_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

CREATE TABLE email_template (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  template_code VARCHAR2(100) NOT NULL,
  subject_template VARCHAR2(500) NOT NULL,
  body_template CLOB NOT NULL,
  channel VARCHAR2(30) DEFAULT 'EMAIL' NOT NULL,
  status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
  create_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
  update_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX uk_email_template_code ON email_template(template_code);

CREATE INDEX idx_notif_delivery_event_id ON notification_delivery_log(event_id);
CREATE INDEX idx_notif_delivery_corr_id ON notification_delivery_log(correlation_id);
CREATE INDEX idx_notif_delivery_user_id ON notification_delivery_log(user_id);
CREATE INDEX idx_notif_delivery_recipient ON notification_delivery_log(recipient);
CREATE INDEX idx_notif_delivery_status ON notification_delivery_log(status);

INSERT INTO email_template (template_code, subject_template, body_template, channel, status, create_at, update_at)
VALUES (
  'PASSWORD_RESET_REQUESTED',
  'Password reset request',
  'We received a password reset request. Your reset token is: {{resetToken}}',
  'EMAIL',
  'ACTIVE',
  SYSTIMESTAMP,
  SYSTIMESTAMP
);

COMMIT;
