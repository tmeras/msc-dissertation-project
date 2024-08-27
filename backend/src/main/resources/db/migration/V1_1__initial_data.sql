/* Populate database with initial data*/
SELECT NEXTVAL('department_seq');
SELECT NEXTVAL('department_seq');
INSERT INTO department (id, name) VALUES (1,'Mechanical Engineering');
INSERT INTO department(id, name) VALUES  (2, 'Computer Science');


SELECT NEXTVAL('role_seq');
SELECT NEXTVAL('role_seq');
INSERT INTO role(id, name) VALUES (1, 'Student');
INSERT INTO role(id, name) VALUES (2, 'Clerical_Staff');
INSERT INTO role(id, name) VALUES (3, 'Academic_Staff');
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
VALUES(1, 'Student A (Dept.1)', 'tmeras@yahoo.gr', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 1, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(2, 'Student B (Dept.1)', 'tmeras1@sheffield.ac.uk', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 1, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(3, 'Student (Dept.2)', 'student3@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 1, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(4, 'Clerical Staff (Dept.1)', 'clstaff1@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 2, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(5, 'Clerical Staff (Dept.2)', 'clstaff2@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 2, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(6, 'Academic Staff A (Dept.1)', 'acstaff1@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(7, 'Academic Staff B (Dept.1)', 'acstaff2@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(8, 'Academic Staff C (Dept.1)', 'acstaff3@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 1);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(9, 'Academic Staff A (Dept.2)', 'acstaff4@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(10, 'Academic Staff B (Dept.2)', 'acstaff5@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(11, 'Academic Staff C (Dept.2)', 'acstaff6@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 3, 2);

INSERT INTO
_user(id, name, email, password, is_approved, role_id, department_id)
VALUES(12, 'Admin', 'admin@gmail.com', '$2a$10$jUQ5ifdVbHENCVx68ZyMSelRnFSJH60YBMP3ejhKi5MLCnw54GpTS', true, 4, 1);


SELECT NEXTVAL('student_information_seq');
SELECT NEXTVAL('student_information_seq');
INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(1, true, false, true, '', 1);

INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(2, false, true, false, 'I have ADHD', 2);

INSERT INTO
student_information(id, has_health_issues, has_disability, has_lsp, additional_details, student_id)
VALUES(3, false, false, false, 'I have PTSD', 3);


SELECT NEXTVAL('ec_application_seq');
SELECT NEXTVAL('ec_application_seq');
INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(1, 'I fell victim to a violent crime', TO_DATE('2024/05/03','YYYY/MM/DD'),TO_DATE('2024/05/08','YYYY/MM/DD'), TO_DATE('2024/05/18','YYYY/MM/DD'), false, true, 1);

INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(2, 'I was in a car accident', TO_DATE('2024/05/12','YYYY/MM/DD'),TO_DATE('2024/05/17','YYYY/MM/DD'), TO_DATE('2024/05/25','YYYY/MM/DD'), false, false, 2);

INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(3, 'My grandmother passed away', TO_DATE('2024/06/15','YYYY/MM/DD'),TO_DATE('2024/06/17','YYYY/MM/DD'), TO_DATE('2024/06/25','YYYY/MM/DD'), false, true, 3);

INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(4, 'I had jury service during this period', TO_DATE('2024/05/22','YYYY/MM/DD'),TO_DATE('2024/05/29','YYYY/MM/DD'), TO_DATE('2024/05/29','YYYY/MM/DD'), true, null, 1);

INSERT INTO
ec_application(id, circumstances_details, affected_date_start, affected_date_end, submitted_on, requires_further_evidence, is_referred, student_id)
VALUES(5, 'I was in the hospital', TO_DATE('2024/06/24','YYYY/MM/DD'),TO_DATE('2024/06/27','YYYY/MM/DD'), TO_DATE('2024/06/27','YYYY/MM/DD'), false, true, 1);


SELECT NEXTVAL('module_outcome_request_seq');
SELECT NEXTVAL('module_outcome_request_seq');
INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(1, 'Deadline Extension', 'Assignment 1' , 1, 'COM1001');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(2, 'Disregard Missing Component Mark', '', 1, 'COM1002');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(3, 'Defer Formal Examination', 'Final Exam', 1, 'COM1003');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(4, 'Disregard Missing Component Mark', '', 2, 'COM1001');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(5, 'Remove Lateness Penalties', 'Assignment 2', 2, 'MEC117');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(6, 'Disregard Missing Component Mark', '', 3, 'COM1002');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(7, 'Disregard Missing Component Mark', '', 4, 'COM1003');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(8, 'Remove Lateness Penalties', 'Team Project', 5, 'MEC117');

INSERT INTO
module_outcome_request(ID, REQUESTED_OUTCOME, RELATED_ASSESSMENT, APPLICATION_ID, MODULE_CODE)
VALUES(9, 'Defer Formal Examination', 'Final Exam', 5, 'MEC113');


SELECT NEXTVAL('module_request_decision_seq');
SELECT NEXTVAL('module_request_decision_seq');
INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(1, 'The incident occurred too long ago', false, 1, 6, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(2, null, true, 1, 7, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(3, 'There was sufficient time to prepare for the assessment', false, 1, 8, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(4, null, true, 2, 6, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(5, null, true, 2, 7, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(6, 'The incident occurred too long ago', false, 3, 6, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(7, 'There was sufficient time to prepare', false, 3, 8, 1);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(8, null, true, 6, 9, 3);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(9, null, true, 6, 10, 3);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(10, null, true, 8, 6, 5);

INSERT INTO
module_request_decision(ID, COMMENTS, IS_APPROVED, MODULE_REQUEST_ID, STAFF_ID, APPLICATION_ID)
VALUES(11, null, true, 8, 7, 5);
