package com.theodoremeras.dissertation.evidence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Integer> {

    List<EvidenceEntity> findAllByEcApplicationId(Integer ecApplicationId);

}
