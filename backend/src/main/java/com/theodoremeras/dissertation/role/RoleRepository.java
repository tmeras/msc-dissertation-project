package com.theodoremeras.dissertation.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    List<RoleEntity> findAllByName(String roleName);

    Optional<RoleEntity> findByName(String roleName);

}
