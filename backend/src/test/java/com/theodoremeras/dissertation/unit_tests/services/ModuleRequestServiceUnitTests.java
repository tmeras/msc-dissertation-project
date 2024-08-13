package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestRepository;
import com.theodoremeras.dissertation.module_request.ModuleRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleRequestServiceUnitTests {

    @Mock
    private ModuleRequestRepository moduleRequestRepository;

    @InjectMocks
    private ModuleRequestService moduleRequestService;

    private ModuleEntity testModuleEntity;

    private EcApplicationEntity testEcApplicationEntity;

    private ModuleRequestEntity testModuleRequestEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(
                TestDataUtil.createTestUserEntityA(TestDataUtil.createTestRoleEntityA(), testDepartmentEntity)
        );
        testModuleRequestEntity = TestDataUtil.createTestRequestEntityA(testEcApplicationEntity, testModuleEntity);
    }

    @Test
    public void testSave() throws Exception {
        when(moduleRequestRepository.save(testModuleRequestEntity)).thenReturn(testModuleRequestEntity);

        ModuleRequestEntity result = moduleRequestService.save(testModuleRequestEntity);

        assertEquals(result, testModuleRequestEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        when(moduleRequestRepository.findAll()).thenReturn(List.of(testModuleRequestEntity));

        List<ModuleRequestEntity> result = moduleRequestService.findAll();

        assertEquals(result, List.of(testModuleRequestEntity));
    }

    @Test
    public void testFindByEcApplicationIdIn() throws Exception {
        when(moduleRequestRepository.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testModuleRequestEntity));

        List<ModuleRequestEntity> result =
                moduleRequestService.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId()));

        assertEquals(result, List.of(testModuleRequestEntity));
    }

    @Test
    public void testFindOneById() throws Exception {
        when(moduleRequestRepository.findById(testModuleRequestEntity.getId()))
                .thenReturn(Optional.of(testModuleRequestEntity));

        Optional<ModuleRequestEntity> result = moduleRequestService.findOneById(testModuleRequestEntity.getId());

        assertEquals(result.get(), testModuleRequestEntity);
    }

    @Test
    public void testExists() throws Exception {
        when(moduleRequestRepository.existsById(testModuleRequestEntity.getId())).thenReturn(true);

        boolean result = moduleRequestService.exists(testModuleRequestEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDelete() throws Exception {
        moduleRequestService.delete(testModuleRequestEntity.getId());

        verify(moduleRequestRepository, times(1)).deleteById(testModuleRequestEntity.getId());
    }

}
