package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
import com.theodoremeras.dissertation.evidence.EvidenceEntity;
import com.theodoremeras.dissertation.evidence.EvidenceRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
public class EvidenceRepositoryUnitTests {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EcApplicationRepository ecApplicationRepository;

    private EcApplicationEntity testEcApplicationEntity;

    private EvidenceEntity testEvidenceEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);

        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(
                testRoleEntity,
                testDepartmentEntity
        );
        userRepository.save(testUserEntity);

        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        ecApplicationRepository.save(testEcApplicationEntity);

        testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(testEcApplicationEntity);
    }

    @Test
    public void testFindAllByEcApplicationId() {
        EvidenceEntity savedEvidenceEntity = evidenceRepository.save(testEvidenceEntity);

        List<EvidenceEntity> result = evidenceRepository.findAllByEcApplicationId(testEcApplicationEntity.getId());

        assertEquals(result, List.of(testEvidenceEntity));
    }

    @Test
    public void testFindById() {
        EvidenceEntity savedEvidenceEntity = evidenceRepository.save(testEvidenceEntity);

        Optional<EvidenceEntity> result = evidenceRepository.findById(savedEvidenceEntity.getId());

        assertEquals(result.get(), testEvidenceEntity);
    }

    @Test
    public void testDeleteById() {
        EvidenceEntity savedEvidenceEntity = evidenceRepository.save(testEvidenceEntity);

        evidenceRepository.deleteById(savedEvidenceEntity.getId());

        assertFalse(evidenceRepository.existsById(savedEvidenceEntity.getId()));
    }

}
