package com.theodoremeras.dissertation;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionRepository;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionService;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestRepository;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleRepository;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserRepository;
import org.springframework.stereotype.Service;

/*
    Service class that aids in creating and saving parent entities into the database
    while testing children entities in order to avoid foreign key constraint violations
 */
@Service
public class ParentCreationService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private DepartmentRepository departmentRepository;

    private EcApplicationRepository ecApplicationRepository;

    private ModuleRepository moduleRepository;

    private ModuleRequestRepository moduleRequestRepository;

    public ParentCreationService(
            UserRepository userRepository, RoleRepository roleRepository,
            DepartmentRepository departmentRepository, EcApplicationRepository ecApplicationRepository,
            ModuleRepository moduleRepository, ModuleRequestRepository moduleRequestRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.ecApplicationRepository = ecApplicationRepository;
        this.moduleRepository = moduleRepository;
        this.moduleRequestRepository = moduleRequestRepository;
    }

    public RoleEntity createRoleParentEntity() {
        return roleRepository.save(TestDataUtil.createTestRoleEntityA());
    }

    public DepartmentEntity createDepartmentParentEntity() {
        return departmentRepository.save(TestDataUtil.createTestDepartmentEntityA());
    }

    public UserEntity createUserParentEntity() {
        RoleEntity role = createRoleParentEntity();
        DepartmentEntity department = createDepartmentParentEntity();

        return userRepository.save(TestDataUtil.createTestUserEntityA(role, department));
    }

    // Alternative user parent entity to avoid violating unique constraints
    public UserEntity createUserParentEntityB() {
        RoleEntity role = createRoleParentEntity();
        DepartmentEntity department = createDepartmentParentEntity();

        return userRepository.save(TestDataUtil.createTestUserEntityB(role, department));
    }

    public ModuleEntity createModuleParentEntity() {
        DepartmentEntity department = createDepartmentParentEntity();

        return moduleRepository.save(TestDataUtil.createTestModuleEntityA(department));
    }

    public EcApplicationEntity createEcApplicationParentEntity() {
        UserEntity student = createUserParentEntityB();

        return ecApplicationRepository.save(TestDataUtil.createTestEcApplicationEntityA(student));
    }

    public ModuleRequestEntity createModuleRequestParentEntity() {
        EcApplicationEntity ecApplication = createEcApplicationParentEntity();
        ModuleEntity module = createModuleParentEntity();

        return moduleRequestRepository.save(TestDataUtil.createTestRequestEntityA(ecApplication, module));
    }

}
