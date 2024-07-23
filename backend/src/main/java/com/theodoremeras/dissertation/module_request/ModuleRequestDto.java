package com.theodoremeras.dissertation.module_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleRequestDto {

    private Integer id;

    private String requestedOutcome;

    private Integer ecApplicationId;

    private String moduleCode;

}
