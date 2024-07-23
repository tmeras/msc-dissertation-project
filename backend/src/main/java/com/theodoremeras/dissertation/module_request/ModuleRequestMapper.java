package com.theodoremeras.dissertation.module_request;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import org.springframework.stereotype.Service;

@Service
public class ModuleRequestMapper {

    public ModuleRequestDto mapToDto(ModuleRequestEntity moduleRequestEntity) {
        return ModuleRequestDto.builder()
                .id(moduleRequestEntity.getId())
                .requestedOutcome(moduleRequestEntity.getRequestedOutcome())
                .ecApplicationId(moduleRequestEntity.getEcApplication().getId())
                .moduleCode(moduleRequestEntity.getModule().getCode())
                .build();
    }

    public ModuleRequestEntity mapFromDto(ModuleRequestDto moduleRequestDto) {
        EcApplicationEntity ecApplication = (moduleRequestDto.getEcApplicationId() == null) ? null :
                EcApplicationEntity.builder()
                        .id(moduleRequestDto.getEcApplicationId())
                        .build();

        ModuleEntity module = (moduleRequestDto.getModuleCode() == null) ? null :
                ModuleEntity.builder()
                        .code(moduleRequestDto.getModuleCode())
                        .build();

        return ModuleRequestEntity.builder()
                .id(moduleRequestDto.getId())
                .requestedOutcome(moduleRequestDto.getRequestedOutcome())
                .ecApplication(ecApplication)
                .module(module)
                .build();
    }

}
