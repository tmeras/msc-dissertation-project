package com.theodoremeras.dissertation.department;

import org.springframework.stereotype.Service;

@Service
public class DepartmentMapper {

    public DepartmentDto mapToDto(DepartmentEntity departmentEntity) {
        return DepartmentDto.builder()
                .id(departmentEntity.getId())
                .name(departmentEntity.getName())
                .build();

    }

    public DepartmentEntity mapFromDto(DepartmentDto departmentDto) {
        return DepartmentEntity.builder()
                .id(departmentDto.getId())
                .name(departmentDto.getName())
                .build();
    }
}
