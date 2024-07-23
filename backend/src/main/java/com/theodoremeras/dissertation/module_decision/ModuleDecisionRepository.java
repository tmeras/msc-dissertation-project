package com.theodoremeras.dissertation.module_decision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleDecisionRepository extends JpaRepository<ModuleDecisionEntity, Integer> {

    List<ModuleDecisionEntity> findAllByModuleRequestId(Integer moduleId);

}
