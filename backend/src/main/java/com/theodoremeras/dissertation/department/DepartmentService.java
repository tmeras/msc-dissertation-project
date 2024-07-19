package com.theodoremeras.dissertation.department;

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

    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }

}
