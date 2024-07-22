package com.theodoremeras.dissertation;

import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.evidence.EvidenceDto;
import com.theodoremeras.dissertation.evidence.EvidenceEntity;
import com.theodoremeras.dissertation.module.ModuleDto;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module_outcome_request.ModuleOutcomeRequestDto;
import com.theodoremeras.dissertation.module_outcome_request.ModuleOutcomeRequestEntity;
import com.theodoremeras.dissertation.role.RoleDto;
import com.theodoremeras.dissertation.role.RoleEntity;
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
                .additionalDetails("Additional details A")
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .isReferred(true)
                .student(student)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoA(
            Integer studentId
    ) {
        return EcApplicationDto.builder()
                .additionalDetails("Additional details A")
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .isReferred(true)
                .studentId(studentId)
                .build();
    }

    public static EcApplicationEntity createTestEcApplicationEntityB(
            UserEntity student
    ) {
        return EcApplicationEntity.builder()
                .additionalDetails("Additional details B")
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .isReferred(false)
                .student(student)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoB(
            Integer studentId
    ) {
        return EcApplicationDto.builder()
                .additionalDetails("Additional details B")
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .isReferred(false)
                .studentId(studentId)
                .build();
    }

    public static ModuleOutcomeRequestEntity createTestRequestEntityA(
            EcApplicationEntity ecApplication, ModuleEntity module
    ) {
        return ModuleOutcomeRequestEntity.builder()
                .ecApplication(ecApplication)
                .module(module)
                .requestedOutcome("Outcome A")
                .build();
    }

    public static ModuleOutcomeRequestDto createTestRequestDtoA(
            Integer ecApplicationId, String moduleCode
    ) {
        return ModuleOutcomeRequestDto.builder()
                .ecApplicationId(ecApplicationId)
                .moduleCode(moduleCode)
                .requestedOutcome("Outcome A")
                .build();
    }

    public static ModuleOutcomeRequestEntity createTestRequestEntityB(
            EcApplicationEntity ecApplication, ModuleEntity module
    ) {
        return ModuleOutcomeRequestEntity.builder()
                .ecApplication(ecApplication)
                .module(module)
                .requestedOutcome("Outcome B")
                .build();
    }

    public static ModuleOutcomeRequestDto createTestRequestDtoB(
            Integer ecApplicationId, String moduleCode
    ) {
        return ModuleOutcomeRequestDto.builder()
                .ecApplicationId(ecApplicationId)
                .moduleCode(moduleCode)
                .requestedOutcome("Outcome B")
                .build();
    }

    public static EvidenceEntity createTestEvidenceEntityA(
            EcApplicationEntity ecApplication
    ) {
        return EvidenceEntity.builder()
                .ecApplication(ecApplication)
                .fileName("File A")
                .build();
    }

    public static EvidenceDto createTestEvidenceDtoA(
            Integer ecApplicationId
    ) {
        return EvidenceDto.builder()
                .ecApplicationId(ecApplicationId)
                .fileName("File A")
                .build();
    }

    public static EvidenceEntity createTestEvidenceEntityB(
            EcApplicationEntity ecApplication
    ) {
        return EvidenceEntity.builder()
                .ecApplication(ecApplication)
                .fileName("File B")
                .build();
    }

    public static EvidenceDto createTestEvidenceDtoB(
            Integer ecApplicationId
    ) {
        return EvidenceDto.builder()
                .ecApplicationId(ecApplicationId)
                .fileName("File B")
                .build();
    }

    public static RoleEntity createTestRoleEntityA() {
        return RoleEntity.builder()
                .name("Role A")
                .build();
    }

    public static RoleDto createTestRoleDtoA() {
        return RoleDto.builder()
                .name("Role A")
                .build();
    }

   public static RoleEntity createTestRoleEntityB() {
        return RoleEntity.builder()
                .name("Role B")
                .build();
    }

    public static RoleDto createTestRoleDtoB() {
        return RoleDto.builder()
                .name("Role B")
                .build();
    }

    public static UserEntity createTestUserEntityA(
            RoleEntity roleEntity, DepartmentEntity departmentEntity
    ) {
        return UserEntity.builder()
                .name("User A")
                .email("userA@gmail.com")
                .password("pass123")
                .isApproved(true)
                .role(roleEntity)
                .department(departmentEntity)
                .build();
    }

    public static UserDto createTestUserDtoA(
            Integer roleId, Integer departmentId
    ) {
        return UserDto.builder()
                .name("User A")
                .email("userA@gmail.com")
                .password("pass123")
                .isApproved(true)
                .roleId(roleId)
                .departmentId(departmentId)
                .build();
    }

    public static UserEntity createTestUserEntityB(
            RoleEntity roleEntity, DepartmentEntity departmentEntity
    ) {
        return UserEntity.builder()
                .name("User B")
                .email("userB@gmail.com")
                .password("pass456")
                .isApproved(true)
                .role(roleEntity)
                .department(departmentEntity)
                .build();
    }

    public static UserDto createTestUserDtoB(
            Integer roleId, Integer departmentId
    ) {
        return UserDto.builder()
                .name("User B")
                .email("userB@gmail.com")
                .password("pass456")
                .isApproved(true)
                .roleId(roleId)
                .departmentId(departmentId)
                .build();
    }



}
