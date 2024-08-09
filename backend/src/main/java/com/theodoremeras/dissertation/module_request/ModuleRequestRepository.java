package com.theodoremeras.dissertation.module_request;


import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRequestRepository extends JpaRepository<ModuleRequestEntity, Integer> {

    List<ModuleRequestEntity> findAllByEcApplicationId(Integer ecApplicationId);

    List<ModuleRequestEntity> findAllByEcApplicationIdIn(List<Integer> ids);

}
