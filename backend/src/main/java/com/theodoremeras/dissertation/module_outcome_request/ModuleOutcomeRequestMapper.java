package com.theodoremeras.dissertation.module_outcome_request;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import org.springframework.stereotype.Component;

@Component
public class ModuleOutcomeRequestMapper {

    public ModuleOutcomeRequestDto mapToDto(ModuleOutcomeRequestEntity moduleOutcomeRequestEntity) {
        return ModuleOutcomeRequestDto.builder()
                .id(moduleOutcomeRequestEntity.getId())
                .requestedOutcome(moduleOutcomeRequestEntity.getRequestedOutcome())
                .ecApplicationId(moduleOutcomeRequestEntity.getEcApplication().getId())
                .moduleCode(moduleOutcomeRequestEntity.getModule().getCode())
                .build();
    }

    public ModuleOutcomeRequestEntity mapFromDto(ModuleOutcomeRequestDto moduleOutcomeRequestDto) {
        EcApplicationEntity ecApplication = (moduleOutcomeRequestDto.getEcApplicationId() == null) ? null :
                EcApplicationEntity.builder()
                        .id(moduleOutcomeRequestDto.getEcApplicationId())
                        .build();

        ModuleEntity module = (moduleOutcomeRequestDto.getModuleCode() == null) ? null :
                ModuleEntity.builder()
                        .code(moduleOutcomeRequestDto.getModuleCode())
                        .build();

        return ModuleOutcomeRequestEntity.builder()
                .id(moduleOutcomeRequestDto.getId())
                .requestedOutcome(moduleOutcomeRequestDto.getRequestedOutcome())
                .ecApplication(ecApplication)
                .module(module)
                .build();
    }

}
