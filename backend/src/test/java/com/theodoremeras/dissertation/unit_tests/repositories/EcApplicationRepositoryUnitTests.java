package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
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
public class EcApplicationRepositoryUnitTests {

    @Autowired
    private EcApplicationRepository ecApplicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUserEntity;

    private EcApplicationEntity testEcApplicationEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);
        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        userRepository.save(testUserEntity);
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
    }

    @Test
    public void testFindAll() {
        ecApplicationRepository.save(testEcApplicationEntity);

        List<EcApplicationEntity> result = ecApplicationRepository.findAll();

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByIdIn() {
        EcApplicationEntity savedEcApplicationEntity = ecApplicationRepository.save(testEcApplicationEntity);

        List<EcApplicationEntity> result =
                ecApplicationRepository.findAllByIdIn(List.of(savedEcApplicationEntity.getId()));

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentId() {
        ecApplicationRepository.save(testEcApplicationEntity);

        List<EcApplicationEntity> result = ecApplicationRepository.findAllByStudentId(testUserEntity.getId());

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentDepartmentId() {
        ecApplicationRepository.save(testEcApplicationEntity);

        List<EcApplicationEntity> result =
                ecApplicationRepository.findAllByStudentDepartmentId(testUserEntity.getDepartment().getId());

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentDepartmentIdAndIsReferred() {
        ecApplicationRepository.save(testEcApplicationEntity);

        List<EcApplicationEntity> result =
                ecApplicationRepository.findAllByStudentDepartmentIdAndIsReferred(
                        testUserEntity.getDepartment().getId(),
                        testEcApplicationEntity.getIsReferred()
                );

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindById() {
        EcApplicationEntity savedEcApplicationEntity = ecApplicationRepository.save(testEcApplicationEntity);

        Optional<EcApplicationEntity> result = ecApplicationRepository.findById(savedEcApplicationEntity.getId());

        assertEquals(result.get(), testEcApplicationEntity);
    }

    @Test
    public void testExistsById() {
        EcApplicationEntity savedEcApplicationEntity = ecApplicationRepository.save(testEcApplicationEntity);

        boolean result = ecApplicationRepository.existsById(savedEcApplicationEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        EcApplicationEntity savedEcApplicationEntity = ecApplicationRepository.save(testEcApplicationEntity);

        ecApplicationRepository.deleteById(savedEcApplicationEntity.getId());

        assertFalse(ecApplicationRepository.existsById(savedEcApplicationEntity.getId()));
    }

}
