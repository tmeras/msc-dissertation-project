package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionRepository;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionService;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.user.UserEntity;
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
public class ModuleDecisionServiceUnitTests {

    @Mock
    private ModuleDecisionRepository moduleDecisionRepository;

    @InjectMocks
    private ModuleDecisionService moduleDecisionService;

    private UserEntity testUserEntity;

    private EcApplicationEntity testEcApplicationEntity;

    private ModuleRequestEntity testModuleRequestEntity;

    private ModuleDecisionEntity testModuleDecisionEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testUserEntity = TestDataUtil.createTestUserEntityA(
                TestDataUtil.createTestRoleEntityA(),
                testDepartmentEntity
        );
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testModuleRequestEntity = TestDataUtil.createTestRequestEntityA(
                testEcApplicationEntity,
                TestDataUtil.createTestModuleEntityA(testDepartmentEntity)
        );
        testModuleDecisionEntity = TestDataUtil.createTestModuleDecisionEntityA(
                testModuleRequestEntity,
                testUserEntity,
                testEcApplicationEntity
        );
    }

    @Test
    public void testSave() throws Exception {
        when(moduleDecisionRepository.save(testModuleDecisionEntity)).thenReturn(testModuleDecisionEntity);

        ModuleDecisionEntity result = moduleDecisionService.save(testModuleDecisionEntity);

        assertEquals(result, testModuleDecisionEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        when(moduleDecisionRepository.findAll()).thenReturn(List.of(testModuleDecisionEntity));

        List<ModuleDecisionEntity> result = moduleDecisionService.findAll();

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByModuleRequestId() throws Exception {
        when(moduleDecisionRepository.findAllByModuleRequestId(testModuleRequestEntity.getId()))
                .thenReturn(List.of(testModuleDecisionEntity));

        List<ModuleDecisionEntity> result =
                moduleDecisionService.findAllByModuleRequestId(testModuleRequestEntity.getId());

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByStaffMemberId() throws Exception {
        when(moduleDecisionRepository.findAllByStaffMemberId(testUserEntity.getId()))
                .thenReturn(List.of(testModuleDecisionEntity));

        List<ModuleDecisionEntity> result = moduleDecisionService.findAllByStaffMemberId(testUserEntity.getId());

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindAllByApplicationIdIn() throws Exception {
        when(moduleDecisionRepository.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testModuleDecisionEntity));

        List<ModuleDecisionEntity> result =
                moduleDecisionService.findAllByApplicationIdIn(List.of(testEcApplicationEntity.getId()));

        assertEquals(result, List.of(testModuleDecisionEntity));
    }

    @Test
    public void testFindOneById() throws Exception {
        when(moduleDecisionRepository.findById(testModuleDecisionEntity.getId()))
                .thenReturn(Optional.of(testModuleDecisionEntity));

        Optional<ModuleDecisionEntity> result = moduleDecisionService.findOneById(testModuleDecisionEntity.getId());

        assertEquals(result.get(), testModuleDecisionEntity);
    }

    @Test
    public void testExists() throws Exception {
        when(moduleDecisionRepository.existsById(testModuleDecisionEntity.getId())).thenReturn(true);

        boolean result = moduleDecisionService.exists(testModuleDecisionEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDelete() throws Exception {
        moduleDecisionService.delete(testModuleDecisionEntity.getId());

        verify(moduleDecisionRepository, times(1))
                .deleteById(testModuleDecisionEntity.getId());
    }

}
