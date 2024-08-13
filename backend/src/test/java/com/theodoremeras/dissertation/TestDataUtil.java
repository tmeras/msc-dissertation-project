package com.theodoremeras.dissertation;

import com.theodoremeras.dissertation.authentication.UserLoginRequestDto;
import com.theodoremeras.dissertation.authentication.UserLoginResponseDto;
import com.theodoremeras.dissertation.authentication.UserRegistrationDto;
import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.evidence.EvidenceDto;
import com.theodoremeras.dissertation.evidence.EvidenceEntity;
import com.theodoremeras.dissertation.module.ModuleDto;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionDto;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestDto;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.role.RoleDto;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.student_information.StudentInformationDto;
import com.theodoremeras.dissertation.student_information.StudentInformationEntity;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserEntity;

import java.time.LocalDate;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static DepartmentEntity createTestDepartmentEntityA() {
        return DepartmentEntity.builder()
                .id(1)
                .name("Engineering")
                .build();
    }

    public static DepartmentDto createTestDepartmentDtoA() {
        return DepartmentDto.builder()
                .id(1)
                .name("Engineering")
                .build();
    }

    public static DepartmentEntity createTestDepartmentEntityB() {
        return DepartmentEntity.builder()
                .id(2)
                .name("Medicine")
                .build();
    }

    public static DepartmentDto createTestDepartmentDtoB() {
        return DepartmentDto.builder()
                .id(2)
                .name("Medicine")
                .build();
    }

    public static ModuleEntity createTestModuleEntityA(DepartmentEntity departmentEntity) {
        return ModuleEntity.builder()
                .code("COM123")
                .name("Software Engineering")
                .department(departmentEntity)
                .build();
    }

    public static ModuleDto createTestModuleDtoA(Integer departmentId) {
        return ModuleDto.builder()
                .code("COM123")
                .name("Software Engineering")
                .departmentId(departmentId)
                .build();
    }

    public static ModuleEntity createTestModuleEntityB(DepartmentEntity departmentEntity) {
        return ModuleEntity.builder()
                .code("COM4567")
                .name("Databases")
                .department(departmentEntity)
                .build();
    }

    public static ModuleDto createTestModuleDtoB(Integer departmentId) {
        return ModuleDto.builder()
                .code("COM4567")
                .name("Databases")
                .departmentId(departmentId)
                .build();
    }

    public static EcApplicationEntity createTestEcApplicationEntityA(
            UserEntity student
    ) {
        return EcApplicationEntity.builder()
                .id(1)
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .submittedOn(LocalDate.of(2024, 5, 15))
                .requiresFurtherEvidence(false)
                .isReferred(true)
                .student(student)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoA(
            Integer studentId
    ) {
        return EcApplicationDto.builder()
                .id(1)
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .submittedOn(LocalDate.of(2024, 5, 15))
                .requiresFurtherEvidence(false)
                .isReferred(true)
                .studentId(studentId)
                .build();
    }

    public static EcApplicationEntity createTestEcApplicationEntityB(
            UserEntity student
    ) {
        return EcApplicationEntity.builder()
                .id(2)
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .submittedOn(LocalDate.of(2024, 2, 3))
                .requiresFurtherEvidence(true)
                .isReferred(false)
                .student(student)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoB(
            Integer studentId
    ) {
        return EcApplicationDto.builder()
                .id(2)
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .submittedOn(LocalDate.of(2024, 2, 3))
                .requiresFurtherEvidence(true)
                .isReferred(false)
                .studentId(studentId)
                .build();
    }

    public static ModuleRequestEntity createTestRequestEntityA(
            EcApplicationEntity ecApplication, ModuleEntity module
    ) {
        return ModuleRequestEntity.builder()
                .id(1)
                .ecApplication(ecApplication)
                .module(module)
                .requestedOutcome("Outcome A")
                .build();
    }

    public static ModuleRequestDto createTestRequestDtoA(
            Integer ecApplicationId, String moduleCode
    ) {
        return ModuleRequestDto.builder()
                .id(1)
                .ecApplicationId(ecApplicationId)
                .moduleCode(moduleCode)
                .requestedOutcome("Outcome A")
                .build();
    }

    public static ModuleRequestEntity createTestRequestEntityB(
            EcApplicationEntity ecApplication, ModuleEntity module
    ) {
        return ModuleRequestEntity.builder()
                .id(2)
                .ecApplication(ecApplication)
                .module(module)
                .requestedOutcome("Outcome B")
                .build();
    }

    public static ModuleRequestDto createTestRequestDtoB(
            Integer ecApplicationId, String moduleCode
    ) {
        return ModuleRequestDto.builder()
                .id(2)
                .ecApplicationId(ecApplicationId)
                .moduleCode(moduleCode)
                .requestedOutcome("Outcome B")
                .build();
    }

    public static EvidenceEntity createTestEvidenceEntityA(
            EcApplicationEntity ecApplication
    ) {
        return EvidenceEntity.builder()
                .id(1)
                .ecApplication(ecApplication)
                .fileName("File A")
                .build();
    }

    public static EvidenceDto createTestEvidenceDtoA(
            Integer ecApplicationId
    ) {
        return EvidenceDto.builder()
                .id(1)
                .ecApplicationId(ecApplicationId)
                .fileName("File A")
                .build();
    }

    public static EvidenceEntity createTestEvidenceEntityB(
            EcApplicationEntity ecApplication
    ) {
        return EvidenceEntity.builder()
                .id(2)
                .ecApplication(ecApplication)
                .fileName("File B")
                .build();
    }

    public static EvidenceDto createTestEvidenceDtoB(
            Integer ecApplicationId
    ) {
        return EvidenceDto.builder()
                .id(2)
                .ecApplicationId(ecApplicationId)
                .fileName("File B")
                .build();
    }

    public static RoleEntity createTestRoleEntityA() {
        return RoleEntity.builder()
                .id(1)
                .name("Role A")
                .build();
    }

    public static RoleDto createTestRoleDtoA() {
        return RoleDto.builder()
                .id(1)
                .name("Role A")
                .build();
    }

    public static RoleEntity createTestRoleEntityB() {
        return RoleEntity.builder()
                .id(2)
                .name("Role B")
                .build();
    }

    public static RoleDto createTestRoleDtoB() {
        return RoleDto.builder()
                .id(2)
                .name("Role B")
                .build();
    }

    public static UserEntity createTestUserEntityA(
            RoleEntity role, DepartmentEntity department
    ) {
        return UserEntity.builder()
                .id(1)
                .name("User A")
                .email("tmeras@yahoo.gr")
                .password("pass123")
                .isApproved(true)
                .role(role)
                .department(department)
                .build();
    }

    public static UserDto createTestUserDtoA(
            Integer roleId, Integer departmentId
    ) {
        return UserDto.builder()
                .id(1)
                .name("User A")
                .email("tmeras@yahoo.gr")
                .isApproved(true)
                .roleId(roleId)
                .departmentId(departmentId)
                .build();
    }

    public static UserEntity createTestUserEntityB(
            RoleEntity role, DepartmentEntity department
    ) {
        return UserEntity.builder()
                .id(2)
                .name("User B")
                .email("userB@gmail.com")
                .password("pass456")
                .isApproved(true)
                .role(role)
                .department(department)
                .build();
    }

    public static UserDto createTestUserDtoB(
            Integer roleId, Integer departmentId
    ) {
        return UserDto.builder()
                .id(2)
                .name("User B")
                .email("userB@gmail.com")
                .isApproved(true)
                .roleId(roleId)
                .departmentId(departmentId)
                .build();
    }

    public static ModuleDecisionEntity createTestModuleDecisionEntityA(
            ModuleRequestEntity moduleRequest, UserEntity staff, EcApplicationEntity ecApplication
    ) {
        return ModuleDecisionEntity.builder()
                .id(1)
                .comments("Comment A")
                .isApproved(false)
                .moduleRequest(moduleRequest)
                .staffMember(staff)
                .ecApplication(ecApplication)
                .build();
    }

    public static ModuleDecisionDto createTestModuleDecisionDtoA(
            Integer moduleRequestId, Integer staffId, Integer ecApplicationId
    ) {
        return ModuleDecisionDto.builder()
                .id(1)
                .comments("Comment A")
                .isApproved(false)
                .moduleRequestId(moduleRequestId)
                .staffMemberId(staffId)
                .ecApplicationId(ecApplicationId)
                .build();
    }

    public static ModuleDecisionEntity createTestModuleDecisionEntityB(
            ModuleRequestEntity moduleRequest, UserEntity staff, EcApplicationEntity ecApplication
    ) {
        return ModuleDecisionEntity.builder()
                .id(2)
                .comments("Comment B")
                .isApproved(true)
                .moduleRequest(moduleRequest)
                .staffMember(staff)
                .ecApplication(ecApplication)
                .build();
    }

    public static ModuleDecisionDto createTestModuleDecisionDtoB(
            Integer moduleRequestId, Integer staffId, Integer ecApplicationId
    ) {
        return ModuleDecisionDto.builder()
                .id(2)
                .comments("Comment B")
                .isApproved(true)
                .moduleRequestId(moduleRequestId)
                .staffMemberId(staffId)
                .ecApplicationId(ecApplicationId)
                .build();
    }

    public static StudentInformationEntity createTestStudentInformationEntityA(
            UserEntity student
    ) {
        return StudentInformationEntity.builder()
                .id(1)
                .hasHealthIssues(true)
                .hasDisability(true)
                .hasLsp(true)
                .additionalDetails("Additional details A")
                .student(student)
                .build();
    }

    public static StudentInformationDto createTestStudentInformationDtoA(
            Integer studentId
    ) {
        return StudentInformationDto.builder()
                .id(1)
                .hasHealthIssues(true)
                .hasDisability(true)
                .hasLsp(true)
                .additionalDetails("Additional details A")
                .studentId(studentId)
                .build();
    }

    public static StudentInformationEntity createTestStudentInformationEntityB(
            UserEntity student
    ) {
        return StudentInformationEntity.builder()
                .id(2)
                .hasHealthIssues(false)
                .hasDisability(false)
                .hasLsp(false)
                .additionalDetails("Additional details B")
                .student(student)
                .build();
    }

    public static StudentInformationDto createTestStudentInformationDtoB(
            Integer studentId
    ) {
        return StudentInformationDto.builder()
                .id(2)
                .hasHealthIssues(false)
                .hasDisability(false)
                .hasLsp(false)
                .additionalDetails("Additional details B")
                .studentId(studentId)
                .build();
    }

    public static UserRegistrationDto createTestUserRegistrationDto(
            Integer roleId, Integer departmentId
    ) {
        return UserRegistrationDto.builder()
                .id(1)
                .name("User A")
                .email("tmeras@yahoo.gr")
                .password("pass123")
                .isApproved(true)
                .roleId(roleId)
                .departmentId(departmentId)
                .build();
    }

    public static UserLoginRequestDto createTestUserLoginRequestDto() {
        return UserLoginRequestDto.builder()
                .email("tmeras@yahoo.gr")
                .password("pass123")
                .build();
    }

    public static UserLoginResponseDto createTestUserLoginResponseDto() {
        return UserLoginResponseDto.builder()
                .jwt("token")
                .user(createTestUserDtoA(1, 1))
                .build();
    }

}
