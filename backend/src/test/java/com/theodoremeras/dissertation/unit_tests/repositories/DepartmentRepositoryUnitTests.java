package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
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
public class DepartmentRepositoryUnitTests {

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentEntity testDepartmentEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
    }

    @Test
    public void testFindAll() {
        departmentRepository.save(testDepartmentEntity);

        List<DepartmentEntity> result = departmentRepository.findAll();

        assertEquals(result, List.of(testDepartmentEntity));
    }

    @Test
    public void testFindById() {
        DepartmentEntity savedDepartmentEntity = departmentRepository.save(testDepartmentEntity);

        Optional<DepartmentEntity> result = departmentRepository.findById(savedDepartmentEntity.getId());

        assertEquals(result.get(), testDepartmentEntity);
    }

    @Test
    public void testExistsById() {
        DepartmentEntity savedDepartmentEntity = departmentRepository.save(testDepartmentEntity);

        boolean result = departmentRepository.existsById(savedDepartmentEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        DepartmentEntity savedDepartmentEntity = departmentRepository.save(testDepartmentEntity);

        departmentRepository.deleteById(savedDepartmentEntity.getId());

        assertFalse(departmentRepository.existsById(savedDepartmentEntity.getId()));
    }

}
