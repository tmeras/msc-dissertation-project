/* Initialize database schema */
CREATE SEQUENCE IF NOT EXISTS _user_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS department_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS ec_application_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS evidence_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS module_outcome_request_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS module_request_decision_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS student_information_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE _user
(
    id            INTEGER NOT NULL,
    name          VARCHAR(255),
    email         VARCHAR(255),
    password      VARCHAR(255),
    is_approved   BOOLEAN,
    role_id       INTEGER NOT NULL,
    department_id INTEGER NOT NULL,
    CONSTRAINT pk__user PRIMARY KEY (id)
);

CREATE TABLE department
(
    id   INTEGER      NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_department PRIMARY KEY (id)
);

CREATE TABLE ec_application
(
    id                    INTEGER NOT NULL,
    circumstances_details VARCHAR(5000),
    additional_details    VARCHAR(5000),
    affected_date_start   date    NOT NULL,
    affected_date_end     date    NOT NULL,
    submitted_on          date    NOT NULL,
    requires_further_evidence BOOLEAN,
    is_referred           BOOLEAN,
    student_id            INTEGER NOT NULL,
    CONSTRAINT pk_ec_application PRIMARY KEY (id)
);

CREATE TABLE evidence
(
    id             INTEGER NOT NULL,
    file_name      VARCHAR(255),
    application_id INTEGER NOT NULL,
    CONSTRAINT pk_evidence PRIMARY KEY (id)
);

CREATE TABLE module
(
    code          VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    department_id INTEGER      NOT NULL,
    CONSTRAINT pk_module PRIMARY KEY (code)
);

CREATE TABLE module_outcome_request
(
    id                INTEGER      NOT NULL,
    requested_outcome VARCHAR(255),
    application_id    INTEGER      NOT NULL,
    module_code       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_module_outcome_request PRIMARY KEY (id)
);

CREATE TABLE module_request_decision
(
    id                INTEGER NOT NULL,
    comments          VARCHAR(5000),
    is_approved       BOOLEAN,
    module_request_id INTEGER NOT NULL,
    staff_id          INTEGER NOT NULL,
    application_id    INTEGER NOT NULL,
    CONSTRAINT pk_module_request_decision PRIMARY KEY (id)
);

CREATE TABLE role
(
    id   INTEGER NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE student_information
(
    id                 INTEGER NOT NULL,
    has_health_issues  BOOLEAN,
    has_disability     BOOLEAN,
    has_lsp            BOOLEAN,
    additional_details VARCHAR(5000),
    student_id         INTEGER NOT NULL,
    CONSTRAINT pk_student_information PRIMARY KEY (id)
);

ALTER TABLE _user
    ADD CONSTRAINT uc__user_email UNIQUE (email);

ALTER TABLE ec_application
    ADD CONSTRAINT FK_EC_APPLICATION_ON_STUDENT FOREIGN KEY (student_id) REFERENCES _user (id);

ALTER TABLE evidence
    ADD CONSTRAINT FK_EVIDENCE_ON_APPLICATION FOREIGN KEY (application_id) REFERENCES ec_application (id);

ALTER TABLE module
    ADD CONSTRAINT FK_MODULE_ON_DEPARTMENT FOREIGN KEY (department_id) REFERENCES department (id);

ALTER TABLE module_outcome_request
    ADD CONSTRAINT FK_MODULE_OUTCOME_REQUEST_ON_APPLICATION FOREIGN KEY (application_id) REFERENCES ec_application (id);

ALTER TABLE module_outcome_request
    ADD CONSTRAINT FK_MODULE_OUTCOME_REQUEST_ON_MODULE_CODE FOREIGN KEY (module_code) REFERENCES module (code);

ALTER TABLE module_request_decision
    ADD CONSTRAINT FK_MODULE_REQUEST_DECISION_ON_APPLICATION FOREIGN KEY (application_id) REFERENCES ec_application (id);

ALTER TABLE module_request_decision
    ADD CONSTRAINT FK_MODULE_REQUEST_DECISION_ON_MODULE_REQUEST FOREIGN KEY (module_request_id) REFERENCES module_outcome_request (id);

ALTER TABLE module_request_decision
    ADD CONSTRAINT FK_MODULE_REQUEST_DECISION_ON_STAFF FOREIGN KEY (staff_id) REFERENCES _user (id);

ALTER TABLE student_information
    ADD CONSTRAINT FK_STUDENT_INFORMATION_ON_STUDENT FOREIGN KEY (student_id) REFERENCES _user (id);

ALTER TABLE _user
    ADD CONSTRAINT FK__USER_ON_DEPARTMENT FOREIGN KEY (department_id) REFERENCES department (id);

ALTER TABLE _user
    ADD CONSTRAINT FK__USER_ON_ROLE FOREIGN KEY (role_id) REFERENCES role (id);
