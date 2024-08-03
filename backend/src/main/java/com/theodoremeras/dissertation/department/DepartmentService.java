package com.theodoremeras.dissertation.department;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    public DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentEntity save(DepartmentEntity departmentEntity) {
        return departmentRepository.save(departmentEntity);
    }

    public List<DepartmentEntity> findAll() {
        return departmentRepository.findAll();
    }

    public Optional<DepartmentEntity> findOneById(Integer id) {
        return departmentRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return departmentRepository.existsById(id);
    }

    public DepartmentEntity partialUpdate(Integer id, DepartmentEntity departmentEntity) {
        departmentEntity.setId(id);

        return departmentRepository.findById(id).map(existingDepartment -> {
            Optional.ofNullable(departmentEntity.getName()).ifPresent(existingDepartment::setName);
            return departmentRepository.save(existingDepartment);
        }).orElseThrow(() -> new RuntimeException("Could not find department with id " + id));
    }

    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }

}
