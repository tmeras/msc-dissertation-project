package com.theodoremeras.dissertation.ec_application;

import com.theodoremeras.dissertation.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EcApplicationMapper {

    public EcApplicationDto mapToDto(EcApplicationEntity ecApplicationEntity) {
        return EcApplicationDto.builder()
                .id(ecApplicationEntity.getId())
                .circumstancesDetails(ecApplicationEntity.getCircumstancesDetails())
                .affectedDateStart(ecApplicationEntity.getAffectedDateStart())
                .affectedDateEnd(ecApplicationEntity.getAffectedDateEnd())
                .submittedOn(ecApplicationEntity.getSubmittedOn())
                .requiresFurtherEvidence(ecApplicationEntity.getRequiresFurtherEvidence())
                .isReferred(ecApplicationEntity.getIsReferred())
                .studentId(ecApplicationEntity.getStudent().getId())
                .build();
    }

    public EcApplicationEntity mapFromDto(EcApplicationDto ecApplicationDto) {
        UserEntity student = (ecApplicationDto.getStudentId() == null) ? null :
                UserEntity.builder()
                        .id(ecApplicationDto.getStudentId())
                        .build();

        return EcApplicationEntity.builder()
                .id(ecApplicationDto.getId())
                .circumstancesDetails(ecApplicationDto.getCircumstancesDetails())
                .affectedDateStart(ecApplicationDto.getAffectedDateStart())
                .affectedDateEnd(ecApplicationDto.getAffectedDateEnd())
                .submittedOn(ecApplicationDto.getSubmittedOn())
                .requiresFurtherEvidence(ecApplicationDto.getRequiresFurtherEvidence())
                .isReferred(ecApplicationDto.getIsReferred())
                .student(student)
                .build();
    }

}
