package com.theodoremeras.dissertation.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    List<RoleEntity> findAllByName(String roleName);

}
