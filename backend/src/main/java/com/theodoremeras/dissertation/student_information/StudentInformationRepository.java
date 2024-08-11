package com.theodoremeras.dissertation.student_information;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentInformationRepository extends JpaRepository<StudentInformationEntity, Integer> {

    Optional<StudentInformationEntity> findByStudentId(Integer studentId);

    Boolean existsByStudentId(Integer studentId);

}
