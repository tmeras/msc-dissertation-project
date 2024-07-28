package com.theodoremeras.dissertation.module_decision;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.user.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class ModuleDecisionMapper {

    public ModuleDecisionDto mapToDto(ModuleDecisionEntity moduleDecisionEntity) {
        return ModuleDecisionDto.builder()
                .id(moduleDecisionEntity.getId())
                .comments(moduleDecisionEntity.getComments())
                .isApproved(moduleDecisionEntity.getIsApproved())
                .moduleRequestId(moduleDecisionEntity.getModuleRequest().getId())
                .staffMemberId(moduleDecisionEntity.getStaffMember().getId())
                .ecApplicationId(moduleDecisionEntity.getEcApplication().getId())
                .build();
    }

    public ModuleDecisionEntity mapFromDto(ModuleDecisionDto moduleDecisionDto) {
        ModuleRequestEntity moduleRequest = (moduleDecisionDto.getModuleRequestId() == null) ? null:
                ModuleRequestEntity.builder()
                        .id(moduleDecisionDto.getModuleRequestId())
                        .build();

        UserEntity staff = (moduleDecisionDto.getStaffMemberId() == null) ? null :
                UserEntity.builder()
                        .id(moduleDecisionDto.getStaffMemberId())
                        .build();

        EcApplicationEntity ecApplication = (moduleDecisionDto.getEcApplicationId() == null) ? null :
                EcApplicationEntity.builder()
                        .id(moduleDecisionDto.getEcApplicationId())
                        .build();

        return ModuleDecisionEntity.builder()
                .id(moduleDecisionDto.getId())
                .comments(moduleDecisionDto.getComments())
                .isApproved(moduleDecisionDto.getIsApproved())
                .moduleRequest(moduleRequest)
                .staffMember(staff)
                .ecApplication(ecApplication)
                .build();
    }

}
