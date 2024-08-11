package com.theodoremeras.dissertation.module;

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
