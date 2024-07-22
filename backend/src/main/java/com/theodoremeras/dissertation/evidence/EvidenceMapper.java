package com.theodoremeras.dissertation.evidence;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import org.springframework.stereotype.Component;

@Component
public class EvidenceMapper {

    public EvidenceDto mapToDto(EvidenceEntity evidenceEntity) {
        return EvidenceDto.builder()
                .id(evidenceEntity.getId())
                .fileName(evidenceEntity.getFileName())
                .ecApplicationId(evidenceEntity.getEcApplication().getId())
                .build();
    }

    public EvidenceEntity mapFromDto(EvidenceDto evidenceDto) {
        EcApplicationEntity ecApplication = (evidenceDto.getEcApplicationId() == null) ? null :
                EcApplicationEntity.builder()
                        .id(evidenceDto.getEcApplicationId())
                        .build();

        return EvidenceEntity.builder()
                .id(evidenceDto.getId())
                .fileName(evidenceDto.getFileName())
                .ecApplication(ecApplication)
                .build();
    }

}
