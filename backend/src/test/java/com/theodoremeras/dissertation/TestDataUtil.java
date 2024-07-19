package com.theodoremeras.dissertation;

import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleDto;
import com.theodoremeras.dissertation.module.ModuleEntity;

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

    public static ModuleDto createTestModuleDtoA(DepartmentDto departmentDto) {
        return ModuleDto.builder()
                .code("COM1234")
                .name("Software Engineering")
                .department(departmentDto)
                .build();
    }

    public static ModuleEntity createTestModuleEntityB(DepartmentEntity departmentEntity) {
        return ModuleEntity.builder()
                .code("COM4567")
                .name("Databases")
                .department(departmentEntity)
                .build();
    }

    public static ModuleDto createTestModuleDtoB(DepartmentDto departmentDto) {
        return ModuleDto.builder()
                .code("COM4567")
                .name("Databases")
                .department(departmentDto)
                .build();
    }

    public static EcApplicationEntity createTestEcApplicationEntityA() {
        return EcApplicationEntity.builder()
                .additionalDetails("Additional details A")
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .isReferred(true)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoA() {
        return EcApplicationDto.builder()
                .additionalDetails("Additional details A")
                .circumstancesDetails("Circumstances details A")
                .affectedDateStart(LocalDate.of(2024, 5, 3))
                .affectedDateEnd(LocalDate.of(2024, 5, 10))
                .isReferred(true)
                .build();
    }

    public static EcApplicationEntity createTestEcApplicationEntityB() {
        return EcApplicationEntity.builder()
                .additionalDetails("Additional details B")
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .isReferred(false)
                .build();
    }

    public static EcApplicationDto createTestEcApplicationDtoB() {
        return EcApplicationDto.builder()
                .additionalDetails("Additional details B")
                .circumstancesDetails("Circumstances details B")
                .affectedDateStart(LocalDate.of(2024, 1, 9))
                .affectedDateEnd(LocalDate.of(2024, 1, 9))
                .isReferred(false)
                .build();
    }


}
