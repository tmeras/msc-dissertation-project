package com.theodoremeras.dissertation.module;

import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty
    private String name;

    @NotNull
    private Integer departmentId;

}
