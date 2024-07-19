package com.theodoremeras.dissertation.department;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    private ModelMapper modelMapper;

    public DepartmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DepartmentDto mapToDto(DepartmentEntity departmentEntity) {
        return modelMapper.map(departmentEntity, DepartmentDto.class);
    }

    public DepartmentEntity mapFromDto(DepartmentDto departmentDto) {
        return modelMapper.map(departmentDto, DepartmentEntity.class);
    }
}
