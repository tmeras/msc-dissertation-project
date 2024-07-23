package com.theodoremeras.dissertation.student_information;

import com.theodoremeras.dissertation.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentInformationRepository extends JpaRepository<StudentInformationEntity, Integer> {

    Optional<StudentInformationEntity> findByStudentId(Integer studentId);

    Boolean existsByStudentId(Integer studentId);

}
