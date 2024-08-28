package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestRepository;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleRepository;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
public class ModuleRequestRepositoryUnitTests {

    @Autowired
    private ModuleRequestRepository moduleRequestRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EcApplicationRepository ecApplicationRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private EcApplicationEntity testEcApplicationEntity;

    private ModuleRequestEntity testModuleRequestEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(
                testRoleEntity,
                testDepartmentEntity
        );
        userRepository.save(testUserEntity);

        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        ecApplicationRepository.save(testEcApplicationEntity);

        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
        moduleRepository.save(testModuleEntity);

        testModuleRequestEntity = TestDataUtil.createTestRequestEntityA(
                testEcApplicationEntity,
                testModuleEntity
        );
    }

    @Test
    public void testFindAll() {
        moduleRequestRepository.save(testModuleRequestEntity);

        List<ModuleRequestEntity> result = moduleRequestRepository.findAll();

        assertEquals(result, List.of(testModuleRequestEntity));
    }

    @Test
    public void testFindAllByEcApplicationIdIn() {
        moduleRequestRepository.save(testModuleRequestEntity);

        List<ModuleRequestEntity> result =
                moduleRequestRepository.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId()));

        assertEquals(result, List.of(testModuleRequestEntity));
    }

    @Test
    public void testFindById() {
        ModuleRequestEntity savedModuleRequestEntity = moduleRequestRepository.save(testModuleRequestEntity);

        Optional<ModuleRequestEntity> result = moduleRequestRepository.findById(savedModuleRequestEntity.getId());

        assertEquals(result.get(), testModuleRequestEntity);
    }

    @Test
    public void testExistsById() {
        ModuleRequestEntity savedModuleRequestEntity = moduleRequestRepository.save(testModuleRequestEntity);

        boolean result = moduleRequestRepository.existsById(savedModuleRequestEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        ModuleRequestEntity savedModuleRequestEntity = moduleRequestRepository.save(testModuleRequestEntity);

        moduleRequestRepository.deleteById(savedModuleRequestEntity.getId());

        assertFalse(moduleRequestRepository.existsById(savedModuleRequestEntity.getId()));
    }

}
