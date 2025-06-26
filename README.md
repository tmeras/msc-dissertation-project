# Extenuating Circumstances Form System
A full-stack web application developed to support the submission and management of students’ extenuating
circumstances applications at the University of Sheffield. Developed using Spring Boot, React, and PostgreSQL as part of my MSc dissertation project.

## 🎯 Project Purpose and Significance
The extenuating circumstances policy and procedure (also known as “additional consideration”, “mitigating” or “special circumstances” procedure) allows students to record any challenging short-term personal circumstances which affected their academic performance, in order for them to be considered when their examination or assessment performance is evaluated. 

Currently, the University of Sheffield has a complicated system for dealing with extenuating circumstances issues. Originally, it was based exclusively on paper and, although it is now partially online, it still proves challenging to use for both students and staff. Specifically, there exists no centralised platform for managing extenuating circumstances applications, as the system instead relies on individual Google Forms submissions, with each department offering its own form. Naturally, this can cause great difficulties in monitoring and managing submissions for both students and staff, especially as the number of applications grows. 

The complexity of the existing system can therefore cause unnecessary delays and even errors when processing applications. Considering the integral role that the extenuating circumstances process has in justly evaluating the academic performance of students, it has become necessary to develop a new system that mitigates the existing difficulties.


## 📋 Features
The primary aim of the project is to design and implement a fully online and easy to use prototype system that could serve as a central platform for managing extenuating circumstances applications, increasing overall efficiency and simplifying the entire process for students and staff members alike. Using this system, the following should be possible:

### Students
- View a list of submitted extenuating circumstances (EC) applications along with their overall progress and status (e.g. under review by academic staff or if more evidence is required)
- View details of a particular EC application after submission, and upload additional evidence if required
- Submit EC applications, specifying their circumstances in detail, the period they were affected, any affected modules, and providing evidence 
- Update personal information relevant to EC applications such as whether they are on a learning support plan or have any disabilities

### Clerical Staff
- View all EC applications submitted by students in the same department along with their status
- Request more evidence from students, if required
- Refer applications to academic staff for further review

### Academic Staff
- View EC applications referred by clerical staff in the same department along with their status
- Decide on individual module requests for a particular application (i.e. to approve or reject them)

### Administrator
- Approve newly registered users
- Manage modules and departments stored in the system


## 🛠️ Tools & Technologies Used
### Frontend
- React (JavaScript)
- TanStack (React) Query
- React Router
- Bootstrap

### Backend
- Spring Boot (Java)
- Spring MVC, Security, Data JPA, Testing
- Flyway (DB migrations)

### Database
- PostgreSQL


## 🧪 Testing
- **Unit Tests**: Isolate and verify business logic in the service layer using JUnit and Mockito
- **Controller Slice Tests**: Lightweight web layer tests using Spring’s @WebMvcTest with MockMvc to simulate HTTP requests
- **Repository Slice Tests**: Database layer tests using Spring’s @DataJpaTest with an in-memory test database
- **Integration Tests**: Loading the full application context using @SpringBootTest with MockMvc to simulate HTTP requests and an in-memory test database


## 🔐 Security 
- Implemented token-based authentication using JSON Web Tokens (JWTs)
- Role-Based Access Control (RBAC) to distinguish between student, academic staff, clerical staff, and admin responsibilities
- User approval by admins
- Cross-Site Scripting (XSS) mitigation by setting Content Security Policy (CSP) header, and React’s built-in output encoding
  

## Entity Relationship Diagram (ERD)
![physical_diagram drawio](https://github.com/user-attachments/assets/5d77cc1e-798e-4c3c-89b1-f2d793348948)

  
## Running Instructions
See _**running_instructions.pdf**_
