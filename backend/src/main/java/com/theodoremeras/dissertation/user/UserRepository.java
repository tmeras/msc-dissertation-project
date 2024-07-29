package com.theodoremeras.dissertation.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByEmail(String email);

    List<UserEntity> findAllByIdIn(List<Integer> ids);
}
