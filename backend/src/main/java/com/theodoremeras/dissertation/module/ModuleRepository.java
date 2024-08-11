package com.theodoremeras.dissertation.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, String> {

    List<ModuleEntity> findAllByCodeIn(List<String> codes);

}
