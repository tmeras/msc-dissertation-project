package com.theodoremeras.dissertation.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    List<UserEntity> findAllByEmail(String email);

    List<UserEntity> findAllByDepartmentIdAndRoleId(Integer departmentId, Integer roleId);

    List<UserEntity> findAllByIdIn(List<Integer> ids);
}
