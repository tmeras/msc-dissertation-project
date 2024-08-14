package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleRepository;
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
public class RoleRepositoryUnitTests {

    @Autowired
    private RoleRepository roleRepository;

    private RoleEntity testRoleEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
    }

    @Test
    public void testFindAll() {
        roleRepository.save(testRoleEntity);

        List<RoleEntity> result = roleRepository.findAll();

        assertEquals(result, List.of(testRoleEntity));
    }

    @Test
    public void testFindAllByRoleName() {
        roleRepository.save(testRoleEntity);

        List<RoleEntity> result = roleRepository.findAllByName(testRoleEntity.getName());

        assertEquals(result, List.of(testRoleEntity));
    }

    @Test
    public void testFindById() {
        RoleEntity savedRoleEntity = roleRepository.save(testRoleEntity);

        Optional<RoleEntity> result = roleRepository.findById(savedRoleEntity.getId());

        assertEquals(result.get(), testRoleEntity);
    }

    @Test
    public void testExistsById() {
        RoleEntity savedRoleEntity = roleRepository.save(testRoleEntity);

        boolean result = roleRepository.existsById(savedRoleEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        RoleEntity savedRoleEntity = roleRepository.save(testRoleEntity);

        roleRepository.deleteById(savedRoleEntity.getId());

        assertFalse(roleRepository.existsById(savedRoleEntity.getId()));
    }

}
