/* Populate database with initial data*/
SELECT NEXTVAL('department_seq');
SELECT NEXTVAL('department_seq');
INSERT INTO department (id, name) VALUES (1,'Mechanical Engineering');
INSERT INTO department(id, name) VALUES  (2, 'Computer Science');


SELECT NEXTVAL('role_seq');
SELECT NEXTVAL('role_seq');
INSERT INTO role(id, name) VALUES (1, 'Student');
INSERT INTO role(id, name) VALUES (2, 'Clerical Staff');
INSERT INTO role(id, name) VALUES (3, 'Academic Staff');
INSERT INTO role(id, name) VALUES (4, 'Administrator');


INSERT INTO module(code, name, department_id) VALUES ('COM1001', 'Introduction to Software Engineering', 2);
INSERT INTO module(code, name, department_id) VALUES ('COM1002', 'Foundations of Computer Science', 2);
INSERT INTO module(code, name, department_id) VALUES ('COM1003', 'Java Programming', 2);
INSERT INTO module(code, name, department_id) VALUES ('MEC117', 'Engineering Fundamentals', 1);
INSERT INTO module(code, name, department_id) VALUES ('MEC113', 'Integrative Engineering Project', 1);


SELECT NEXTVAL('_user_seq');
SELECT NEXTVAL('_user_seq');
INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(1, 'Student 1', 'student1@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 1, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(2, 'Student 2', 'student2@gmail.com', 'pass123', true, 1, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(3, 'Student 3', 'student3@gmail.com', 'pass123', true, 1, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(4, 'Clerical Staff 1', 'clstaff1@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 2, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(5, 'Clerical Staff 2', 'clstaff2@gmail.com', 'pass123', true, 2, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(6, 'Clerical Staff 3', 'clstaff3@gmail.com', 'pass123', true, 2, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(7, 'Academic Staff 1', 'acstaff1@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(8, 'Academic Staff 2', 'acstaff2@gmail.com', 'pass123', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(9, 'Academic Staff 3', 'acstaff3@gmail.com', 'pass123', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(10, 'Academic Staff 4', 'acstaff4@gmail.com', 'pass123', true, 3, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(11, 'Academic Staff 5', 'acstaff5@gmail.com', 'pass123', true, 3, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(12, 'Admin', 'admin@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 4, 1);

SELECT NEXTVAL('student_information_seq');
SELECT NEXTVAL('student_information_seq');
INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(1, true, false, true, null, 1);

INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(2, false, true, false, 'Student suffers from PTSD', 2);

INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(3, false, false, false, null, 3);


SELECT NEXTVAL('ec_application_seq');
SELECT NEXTVAL('ec_application_seq');
INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(1, 'I fell victim to a violent crime', TO_DATE('2024/05/03','YYYY/MM/DD'),TO_DATE('2024/05/08','YYYY/MM/DD'), TO_DATE('2024/05/18','YYYY/MM/DD'), false, false, 1);

INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(2, 'I was in a car accident', TO_DATE('2024/06/12','YYYY/MM/DD'),TO_DATE('2024/6/17','YYYY/MM/DD'), TO_DATE('2024/6/25','YYYY/MM/DD'), true, false, 2);


SELECT NEXTVAL('module_outcome_request_seq');
SELECT NEXTVAL('module_outcome_request_seq');
INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, APPLICATION_ID, MODULE_CODE)
VALUES(1, 'Deadline Extension', 1, 'COM1001');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, APPLICATION_ID, MODULE_CODE)
VALUES(2, 'Disregard Missing Component Mark', 1, 'COM1002')

