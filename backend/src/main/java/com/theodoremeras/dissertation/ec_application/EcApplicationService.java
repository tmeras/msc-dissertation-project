package com.theodoremeras.dissertation.ec_application;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EcApplicationService {

    private EcApplicationRepository ecApplicationRepository;


    public EcApplicationService(EcApplicationRepository ecApplicationRepository) {
        this.ecApplicationRepository = ecApplicationRepository;
    }

    public EcApplicationEntity save(EcApplicationEntity ecApplicationEntity) {
        return ecApplicationRepository.save(ecApplicationEntity);
    }

    public List<EcApplicationEntity> findAll() {
        return ecApplicationRepository.findAll();
    }

    public List<EcApplicationEntity> findAllByIdIn(List<Integer> ids) {
        return ecApplicationRepository.findAllByIdIn(ids);
    }

    public List<EcApplicationEntity> findAllByStudentId(Integer studentId) {
        return ecApplicationRepository.findAllByStudentId(studentId);
    }

    public List<EcApplicationEntity> findAllByStudentDepartmentId(Integer departmentId) {
        return ecApplicationRepository.findAllByStudentDepartmentId(departmentId);
    }

    public Optional<EcApplicationEntity> findOneById(Integer id) {
        return ecApplicationRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return ecApplicationRepository.existsById(id);
    }

    public EcApplicationEntity partialUpdate(Integer id, EcApplicationEntity ecApplicationEntity) {
        ecApplicationEntity.setId(id);

        return ecApplicationRepository.findById(id).map(existingEcApplication -> {
            Optional.ofNullable(ecApplicationEntity.getAdditionalDetails()).
                    ifPresent(existingEcApplication::setAdditionalDetails);
            Optional.ofNullable(ecApplicationEntity.getCircumstancesDetails()).
                    ifPresent(existingEcApplication::setCircumstancesDetails);
            Optional.ofNullable(ecApplicationEntity.getAffectedDateStart()).
                    ifPresent(existingEcApplication::setAffectedDateStart);
            Optional.ofNullable(ecApplicationEntity.getAffectedDateEnd()).
                    ifPresent(existingEcApplication::setAffectedDateEnd);
            Optional.ofNullable(ecApplicationEntity.getIsReferred()).
                    ifPresent(existingEcApplication::setIsReferred);
            return ecApplicationRepository.save(existingEcApplication);
        }).orElseThrow(() -> new RuntimeException("Could not find EC application with id " + id));
    }

    public void delete(Integer id) {
        ecApplicationRepository.deleteById(id);
    }

}
