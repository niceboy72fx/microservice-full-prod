-- tao multi schema
CREATE USER auth_db IDENTIFIED BY "123456";

GRANT CONNECT, RESOURCE TO auth_db;

ALTER USER auth_db QUOTA UNLIMITED ON USERS;

ALTER SESSION SET CURRENT_SCHEMA = auth_db;


CREATE TABLE user (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  full_name VARCHAR2(50) NOT NULL,
  email VARCHAR2(100) NOT NULL,
  password VARCHAR2(255),
  status VARCHAR2(100),
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE role (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  name VARCHAR2(150),
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE permissions (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  name VARCHAR2(150),
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE user_role (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  user_id VARCHAR2(32),
  role_id VARCHAR2(32),
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE role_permissions (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  permissions_id VARCHAR2(32),
  role_id VARCHAR2(32),
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE oauth_provider (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  user_id VARCHAR2(32),
  provider_name VARCHAR2(50)
);

CREATE TABLE app_user_2fa (
  email VARCHAR2(100) PRIMARY KEY,
  secret VARCHAR2(255) NOT NULL,
  enabled NUMBER(1) DEFAULT 0 NOT NULL,
  create_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE app_user_login_audit (
  id VARCHAR2(32) DEFAULT RAWTOHEX(SYS_GUID()) PRIMARY KEY,
  email VARCHAR2(100) NOT NULL,
  ip_address VARCHAR2(64),
  success NUMBER(1) DEFAULT 0 NOT NULL,
  reason VARCHAR2(120),
  create_at TIMESTAMP
);

INSERT INTO app_user (full_name, email, password, status, create_at, update_at)
VALUES ('Admin User', 'admin@example.com', '', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO app_user (full_name, email, password, status, create_at, update_at)
VALUES ('Normal User', 'user@example.com', '', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP);


INSERT INTO role (name, create_at, update_at)
VALUES ('ADMIN', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO role (name, create_at, update_at)
VALUES ('USER', SYSTIMESTAMP, SYSTIMESTAMP);


INSERT INTO permissions (name, create_at, update_at)
VALUES ('CREATE_USER', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO permissions (name, create_at, update_at)
VALUES ('UPDATE_USER', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO permissions (name, create_at, update_at)
VALUES ('DELETE_USER', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO permissions (name, create_at, update_at)
VALUES ('VIEW_USER', SYSTIMESTAMP, SYSTIMESTAMP);


INSERT INTO role_permissions (permissions_id, role_id, create_at, update_at)
SELECT p.id, r.id, SYSTIMESTAMP, SYSTIMESTAMP
FROM permissions p
CROSS JOIN role r
WHERE r.name = 'ADMIN';


INSERT INTO role_permissions (permissions_id, role_id, create_at, update_at)
SELECT p.id, r.id, SYSTIMESTAMP, SYSTIMESTAMP
FROM permissions p
CROSS JOIN role r
WHERE r.name = 'USER'
AND p.name = 'VIEW_USER';


INSERT INTO user_role (user_id, role_id, create_at, update_at)
SELECT u.id, r.id, SYSTIMESTAMP, SYSTIMESTAMP
FROM app_user u
CROSS JOIN role r
WHERE u.email = 'admin@example.com'
AND r.name = 'ADMIN';


INSERT INTO user_role (user_id, role_id, create_at, update_at)
SELECT u.id, r.id, SYSTIMESTAMP, SYSTIMESTAMP
FROM app_user u
CROSS JOIN role r
WHERE u.email = 'user@example.com'
AND r.name = 'USER';

COMMIT;
