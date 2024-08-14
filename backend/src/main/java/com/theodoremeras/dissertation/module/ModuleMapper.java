package com.theodoremeras.dissertation.module;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import org.springframework.stereotype.Service;


@Service
public class ModuleMapper {

    public ModuleDto mapToDto(ModuleEntity moduleEntity) {
        return ModuleDto.builder()
                .name(moduleEntity.getName())
                .code(moduleEntity.getCode())
                .departmentId(moduleEntity.getDepartment().getId())
                .build();
    }

    public ModuleEntity mapFromDto(ModuleDto moduleDto) {
        DepartmentEntity department = (moduleDto.getDepartmentId() == null) ? null :
                DepartmentEntity.builder().id(moduleDto.getDepartmentId()).build();

        return ModuleEntity.builder()
                .name(moduleDto.getName())
                .code(moduleDto.getCode())
                .department(department)
                .build();
    }

}
