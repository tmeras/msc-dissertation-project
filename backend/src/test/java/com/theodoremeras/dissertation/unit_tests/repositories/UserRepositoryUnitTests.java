package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
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
public class UserRepositoryUnitTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private RoleEntity testRoleEntity;

    private DepartmentEntity testDepartmentEntity;

    private UserEntity testUserEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);

        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
    }

    @Test
    public void testFindAll() {
        userRepository.save(testUserEntity);

        List<UserEntity> result = userRepository.findAll();

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByEmail() {
        userRepository.save(testUserEntity);

        List<UserEntity> result = userRepository.findAllByEmail(testUserEntity.getEmail());

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByDepartmentIdAndRoleId() {
        userRepository.save(testUserEntity);

        List<UserEntity> result =
                userRepository.findAllByDepartmentIdAndRoleId(testDepartmentEntity.getId(), testRoleEntity.getId());

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByIdIn() {
        UserEntity savedUserEntity = userRepository.save(testUserEntity);

        List<UserEntity> result = userRepository.findAllByIdIn(List.of(savedUserEntity.getId()));

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindById() {
        UserEntity savedUserEntity = userRepository.save(testUserEntity);

        Optional<UserEntity> result = userRepository.findById(savedUserEntity.getId());

        assertEquals(result.get(), testUserEntity);
    }

    @Test
    public void testFindByEmail() {
        userRepository.save(testUserEntity);

        Optional<UserEntity> result = userRepository.findByEmail(testUserEntity.getEmail());

        assertEquals(result.get(), testUserEntity);
    }

    @Test
    public void testExistsById() {
        UserEntity savedUserEntity = userRepository.save(testUserEntity);

        boolean result =  userRepository.existsById(savedUserEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        UserEntity savedUserEntity = userRepository.save(testUserEntity);

        userRepository.deleteById(savedUserEntity.getId());

        assertFalse(userRepository.existsById(savedUserEntity.getId()));
    }


}
