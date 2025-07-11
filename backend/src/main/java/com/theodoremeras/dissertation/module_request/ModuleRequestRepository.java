package com.theodoremeras.dissertation.module_request;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRequestRepository extends JpaRepository<ModuleRequestEntity, Integer> {

    List<ModuleRequestEntity> findAllByEcApplicationIdIn(List<Integer> ids);

}
