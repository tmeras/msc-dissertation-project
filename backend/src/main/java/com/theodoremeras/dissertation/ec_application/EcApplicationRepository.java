package com.theodoremeras.dissertation.ec_application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcApplicationRepository extends JpaRepository<EcApplicationEntity, Integer> {

    List<EcApplicationEntity> findByIdIn(List<Integer> ids);

}
