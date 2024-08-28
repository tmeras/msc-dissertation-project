package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationRepository;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserEntity;
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
public class EcApplicationServiceUnitTests {

    @Mock
    private EcApplicationRepository ecApplicationRepository;

    @InjectMocks
    private EcApplicationService ecApplicationService;

    private UserEntity testUserEntity;

    private EcApplicationEntity testEcApplicationEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
    }

    @Test
    public void testSave() {
        when(ecApplicationRepository.save(testEcApplicationEntity)).thenReturn(testEcApplicationEntity);

        EcApplicationEntity result = ecApplicationService.save(testEcApplicationEntity);

        assertEquals(result, testEcApplicationEntity);
    }

    @Test
    public void testFindAll() {
        when(ecApplicationRepository.findAll()).thenReturn(List.of(testEcApplicationEntity));

        List<EcApplicationEntity> result = ecApplicationService.findAll();

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByIdIn() {
        when(ecApplicationRepository.findAllByIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testEcApplicationEntity));

        List<EcApplicationEntity> result =
                ecApplicationService.findAllByIdIn(List.of(testEcApplicationEntity.getId()));

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentId() {
        when(ecApplicationRepository.findAllByStudentId(testUserEntity.getId()))
                .thenReturn(List.of(testEcApplicationEntity));

        List<EcApplicationEntity> result = ecApplicationService.findAllByStudentId(testUserEntity.getId());

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentDepartmentId() {
        when(ecApplicationRepository.findAllByStudentDepartmentId(testUserEntity.getDepartment().getId()))
                .thenReturn(List.of(testEcApplicationEntity));

        List<EcApplicationEntity> result =
                ecApplicationService.findAllByStudentDepartmentId(testUserEntity.getDepartment().getId());

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindAllByStudentDepartmentIdAndIsReferred() {
        when(ecApplicationRepository
                .findAllByStudentDepartmentIdAndIsReferred(
                        testUserEntity.getDepartment().getId(),
                        testEcApplicationEntity.getIsReferred()
                )
        ).thenReturn(List.of(testEcApplicationEntity));

        List<EcApplicationEntity> result =
                ecApplicationService.findAllByStudentDepartmentIdAndIsReferred(
                        testUserEntity.getDepartment().getId(),
                        testEcApplicationEntity.getIsReferred()
                );

        assertEquals(result, List.of(testEcApplicationEntity));
    }

    @Test
    public void testFindOneById() {
        when(ecApplicationRepository.findById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));

        Optional<EcApplicationEntity> result = ecApplicationService.findOneById(testEcApplicationEntity.getId());

        assertEquals(result.get(), testEcApplicationEntity);
    }

    @Test
    public void testExists() {
        when(ecApplicationRepository.existsById(testEcApplicationEntity.getId())).thenReturn(true);

        boolean result = ecApplicationService.exists(testEcApplicationEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testPartialUpdate() {
        EcApplicationEntity updatedEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityB(testUserEntity);

        when(ecApplicationRepository.findById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));
        when(ecApplicationRepository.save(testEcApplicationEntity)).thenReturn(updatedEcApplicationEntity);

        EcApplicationEntity result =
                ecApplicationService.partialUpdate(testEcApplicationEntity.getId(), updatedEcApplicationEntity);

        assertEquals(result, updatedEcApplicationEntity);
    }

    @Test
    public void testPartialUpdateWhenNoApplicationExists() {
        EcApplicationEntity updatedEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityB(testUserEntity);

        when(ecApplicationRepository.findById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ecApplicationService.partialUpdate(testEcApplicationEntity.getId(), updatedEcApplicationEntity));
    }

    @Test
    public void testDelete() {
        ecApplicationService.delete(testEcApplicationEntity.getId());

        verify(ecApplicationRepository, times(1)).deleteById(testEcApplicationEntity.getId());
    }

}
