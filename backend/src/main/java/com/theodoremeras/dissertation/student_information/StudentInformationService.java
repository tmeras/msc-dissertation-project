package com.theodoremeras.dissertation.student_information;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentInformationService {

    private final StudentInformationRepository studentInformationRepository;

    public StudentInformationService(StudentInformationRepository studentInformationRepository) {
        this.studentInformationRepository = studentInformationRepository;
    }

    public StudentInformationEntity save(StudentInformationEntity studentInformationEntity) {
        return studentInformationRepository.save(studentInformationEntity);
    }

    public List<StudentInformationEntity> findAll() {
        return studentInformationRepository.findAll();
    }

    public Optional<StudentInformationEntity> findOneById(Integer id) {
        return studentInformationRepository.findById(id);
    }

    public Optional<StudentInformationEntity> findOneByStudentId(Integer id) {
        return studentInformationRepository.findByStudentId(id);
    }

    public Boolean exists(Integer id) {
        return studentInformationRepository.existsById(id);
    }

    public StudentInformationEntity partialUpdate(Integer id, StudentInformationEntity studentInformationEntity) {
        studentInformationEntity.setId(id);

        return studentInformationRepository.findById(id).map(existingStudentData -> {
            Optional.ofNullable(studentInformationEntity.getHasHealthIssues())
                    .ifPresent(existingStudentData::setHasHealthIssues);
            Optional.ofNullable(studentInformationEntity.getHasDisability())
                    .ifPresent(existingStudentData::setHasDisability);
            Optional.ofNullable(studentInformationEntity.getHasLsp())
                    .ifPresent(existingStudentData::setHasLsp);
            Optional.ofNullable(studentInformationEntity.getAdditionalDetails())
                    .ifPresent(existingStudentData::setAdditionalDetails);
            return studentInformationRepository.save(existingStudentData);
        }).orElseThrow(() -> new RuntimeException("Could not find student information with id " + id));
    }

    public void delete(Integer id) {
        studentInformationRepository.deleteById(id);
    }

}
