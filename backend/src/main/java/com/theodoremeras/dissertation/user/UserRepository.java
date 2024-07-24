package com.theodoremeras.dissertation.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    public Boolean existsByEmail(String email);
}
