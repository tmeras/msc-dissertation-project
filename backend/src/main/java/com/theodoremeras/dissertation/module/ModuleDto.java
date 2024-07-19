package com.theodoremeras.dissertation.module;

import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDto {

    private String code;

    private String name;

    private DepartmentDto department;

}
