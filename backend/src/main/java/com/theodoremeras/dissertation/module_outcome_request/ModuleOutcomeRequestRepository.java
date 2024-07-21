package com.theodoremeras.dissertation.module_outcome_request;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleOutcomeRequestRepository
        extends JpaRepository<ModuleOutcomeRequestEntity, Integer> {

    List<ModuleOutcomeRequestEntity> findAllByEcApplicationId(Integer ecApplicationId);

}
