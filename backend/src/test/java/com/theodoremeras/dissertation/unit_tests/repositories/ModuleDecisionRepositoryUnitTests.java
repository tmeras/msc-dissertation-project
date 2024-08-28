package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionRepository;
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
public class ModuleDecisionRepositoryUnitTests {

    @Autowired
    private ModuleDecisionRepository moduleDecisionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EcApplicationRepository ecApplicationRepository;

    @Autowired
    private ModuleRequestRepository moduleRequestRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private UserEntity testUserEntity;

    private EcApplicationEntity testEcApplicationEntity;

    private ModuleRequestEntity testModuleRequestEntity;

    private ModuleDecisionEntity testModuleDecisionEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);

        testUserEntity = TestDataUtil.createTestUserEntityA(
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
        moduleRequestRepository.save(testModuleRequestEntity);

        testModuleDecisionEntity = TestDataUtil.createTestModuleDecisionEntityA(
                testModuleRequestEntity,
                testUserEntity,
                testEcApplicationEntity
        );
    }

    @Test
    public void testFindAll() {
        moduleDecisionRepository.save(testModuleDecisionEntity);

        List<ModuleDecisionEntity> result = moduleDecisionRepository.findAll();

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByModuleRequestId() {
        moduleDecisionRepository.save(testModuleDecisionEntity);

        List<ModuleDecisionEntity> result =
                moduleDecisionRepository.findAllByModuleRequestId(testModuleRequestEntity.getId());

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByStaffMemberId() {
        moduleDecisionRepository.save(testModuleDecisionEntity);

        List<ModuleDecisionEntity> result =
                moduleDecisionRepository.findAllByStaffMemberId(testUserEntity.getId());

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByEcApplicationId() {
        moduleDecisionRepository.save(testModuleDecisionEntity);

        List<ModuleDecisionEntity> result =
                moduleDecisionRepository.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId()));

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindById() {
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionRepository.save(testModuleDecisionEntity);

        Optional<ModuleDecisionEntity> result = moduleDecisionRepository.findById(savedModuleDecisionEntity.getId());

        assertEquals(result.get(), testModuleDecisionEntity);
    }

    @Test
    public void testExistsById() {
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionRepository.save(testModuleDecisionEntity);

        boolean result = moduleDecisionRepository.existsById(savedModuleDecisionEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionRepository.save(testModuleDecisionEntity);

        moduleDecisionRepository.deleteById(savedModuleDecisionEntity.getId());

        assertFalse(moduleDecisionRepository.existsById(savedModuleDecisionEntity.getId()));
    }

}
