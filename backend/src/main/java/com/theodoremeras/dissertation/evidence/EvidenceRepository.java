package com.theodoremeras.dissertation.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Integer> {

    List<EvidenceEntity> findAllByEcApplicationId(Integer ecApplicationId);

}
