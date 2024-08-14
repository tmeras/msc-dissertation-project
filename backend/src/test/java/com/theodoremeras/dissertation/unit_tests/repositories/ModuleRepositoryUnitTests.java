package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import org.junit.jupiter.api.AfterEach;
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
public class ModuleRepositoryUnitTests {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private ModuleEntity testModuleEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
    }

    @Test
    public void testFindAll() {
        moduleRepository.save(testModuleEntity);

        List<ModuleEntity> result = moduleRepository.findAll();

        assertEquals(result, List.of(testModuleEntity));
    }

    @Test
    public void testFindAllByCodeIn() {
        moduleRepository.save(testModuleEntity);

        List<ModuleEntity> result = moduleRepository.findAllByCodeIn(List.of(testModuleEntity.getCode()));

        assertEquals(result, List.of(testModuleEntity));
    }

    @Test
    public void testFindById() {
        ModuleEntity savedModuleEntity = moduleRepository.save(testModuleEntity);

        Optional<ModuleEntity> result = moduleRepository.findById(savedModuleEntity.getCode());

        assertEquals(result.get(), testModuleEntity);
    }

    @Test
    public void testExistsById() {
        ModuleEntity savedModuleEntity = moduleRepository.save(testModuleEntity);

        boolean result = moduleRepository.existsById(savedModuleEntity.getCode());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        ModuleEntity savedModuleEntity = moduleRepository.save(testModuleEntity);

        moduleRepository.deleteById(savedModuleEntity.getCode());

        assertFalse(moduleRepository.existsById(savedModuleEntity.getCode()));
    }

}
