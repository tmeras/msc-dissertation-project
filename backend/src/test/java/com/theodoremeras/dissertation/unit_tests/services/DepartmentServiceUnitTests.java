package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.department.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceUnitTests {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private DepartmentEntity testDepartmentEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
    }

    @Test
    public void testSave() {
        when(departmentRepository.save(testDepartmentEntity)).thenReturn(testDepartmentEntity);

        DepartmentEntity result = departmentService.save(testDepartmentEntity);

        assertEquals(result, testDepartmentEntity);
    }

    @Test
    public void testFindAll() {
        when(departmentRepository.findAll()).thenReturn(List.of(testDepartmentEntity));

        List<DepartmentEntity> result = departmentService.findAll();

        assertEquals(result, List.of(testDepartmentEntity));
    }

    @Test
    public void testFindOneById() {
        when(departmentRepository.findById(testDepartmentEntity.getId())).
                thenReturn(Optional.of(testDepartmentEntity));

        Optional<DepartmentEntity> result = departmentService.findOneById(testDepartmentEntity.getId());

        assertEquals(result.get(), testDepartmentEntity);
    }

    @Test
    public void testExists() {
        when(departmentRepository.existsById(testDepartmentEntity.getId())).thenReturn(true);

        boolean result = departmentService.exists(testDepartmentEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testPartialUpdate() {
        DepartmentEntity updatedDepartmentEntity = TestDataUtil.createTestDepartmentEntityB();

        when(departmentRepository.findById(testDepartmentEntity.getId())).
                thenReturn(Optional.of(testDepartmentEntity));
        when(departmentRepository.save(testDepartmentEntity)).thenReturn(updatedDepartmentEntity);

        DepartmentEntity result =
                departmentService.partialUpdate(testDepartmentEntity.getId(), updatedDepartmentEntity);

        assertEquals(result, updatedDepartmentEntity);
    }

    @Test
    public void testPartialUpdateWhenNoDepartmentExists() {
        DepartmentEntity updatedDepartmentEntity = TestDataUtil.createTestDepartmentEntityB();

        when(departmentRepository.findById(testDepartmentEntity.getId())).
                thenReturn(Optional.empty());


        assertThrows(RuntimeException.class,
                () -> departmentService.partialUpdate(testDepartmentEntity.getId(), updatedDepartmentEntity));
    }

    @Test
    public void testDelete() {
        departmentService.delete(testDepartmentEntity.getId());

        verify(departmentRepository, times(1)).deleteById(testDepartmentEntity.getId());
    }

}
