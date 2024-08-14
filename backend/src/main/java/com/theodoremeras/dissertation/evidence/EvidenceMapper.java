package com.theodoremeras.dissertation.evidence;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import org.springframework.stereotype.Service;

@Service
public class EvidenceMapper {

    public EvidenceDto mapToDto(EvidenceEntity evidenceEntity) {
        return EvidenceDto.builder()
                .id(evidenceEntity.getId())
                .fileName(evidenceEntity.getFileName())
                .ecApplicationId(evidenceEntity.getEcApplication().getId())
                .build();
    }

}
