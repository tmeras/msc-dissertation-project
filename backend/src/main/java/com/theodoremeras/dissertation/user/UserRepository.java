package com.theodoremeras.dissertation.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    List<UserEntity> findAllByEmail(String email);

    List<UserEntity> findAllByDepartmentIdAndRoleId(Integer departmentId, Integer roleId);

    List<UserEntity> findAllByIdIn(List<Integer> ids);

    Optional<UserEntity> findByEmail(String email);

}
