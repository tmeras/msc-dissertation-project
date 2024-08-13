package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import com.theodoremeras.dissertation.module.ModuleService;
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
public class ModuleServiceUnitTests {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleService moduleService;

    private ModuleEntity testModuleEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
    }

    @Test
    public void testSave() throws Exception {
        when(moduleRepository.save(testModuleEntity)).thenReturn(testModuleEntity);

        ModuleEntity result = moduleService.save(testModuleEntity);

        assertEquals(result, testModuleEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        when(moduleRepository.findAll()).thenReturn(List.of(testModuleEntity));

        List<ModuleEntity> result = moduleService.findAll();

        assertEquals(result, List.of(testModuleEntity));
    }

    @Test
    public void testFindAllByModuleCodeIn() throws Exception {
        when(moduleRepository.findAllByCodeIn(List.of(testModuleEntity.getCode())))
                .thenReturn(List.of(testModuleEntity));

        List<ModuleEntity> result = moduleService.findAllByModuleCodeIn(List.of(testModuleEntity.getCode()));

        assertEquals(result, List.of(testModuleEntity));
    }

    @Test
    public void testFindOneByCode() throws Exception {
        when(moduleRepository.findById(testModuleEntity.getCode())).thenReturn(Optional.of(testModuleEntity));

        Optional<ModuleEntity> result = moduleService.findOneByCode(testModuleEntity.getCode());

        assertEquals(result.get(), testModuleEntity);
    }

    @Test
    public void testExists() throws Exception {
        when(moduleRepository.existsById(testModuleEntity.getCode())).thenReturn(true);

        boolean result = moduleService.exists(testModuleEntity.getCode());

        assertTrue(result);
    }

    @Test
    public void testPartialUpdate() throws Exception {
        when(moduleRepository.findById(testModuleEntity.getCode())).thenReturn(Optional.of(testModuleEntity));
        when(moduleRepository.save(testModuleEntity)).thenReturn(testModuleEntity);

        ModuleEntity result = moduleService.partialUpdate(testModuleEntity.getCode(), testModuleEntity);

        assertEquals(result, testModuleEntity);
    }

    @Test
    public void testPartialUpdateWhenNoModuleExists() throws Exception {
        when(moduleRepository.findById(testModuleEntity.getCode())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> moduleService.partialUpdate(testModuleEntity.getCode(), testModuleEntity));
    }
    
    @Test
    public void testDelete() throws Exception {
        moduleService.delete(testModuleEntity.getCode());

        verify(moduleRepository, times(1)).deleteById(testModuleEntity.getCode());
    }

}
