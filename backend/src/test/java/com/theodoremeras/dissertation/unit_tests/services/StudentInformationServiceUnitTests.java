package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.student_information.StudentInformationEntity;
import com.theodoremeras.dissertation.student_information.StudentInformationRepository;
import com.theodoremeras.dissertation.student_information.StudentInformationService;
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
public class StudentInformationServiceUnitTests {

    @Mock
    private StudentInformationRepository studentInformationRepository;

    @InjectMocks
    private StudentInformationService studentInformationService;

    private UserEntity testUserEntity;

    private StudentInformationEntity testStudentInformationEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testUserEntity = TestDataUtil.createTestUserEntityA(
                TestDataUtil.createTestRoleEntityA(),
                TestDataUtil.createTestDepartmentEntityA()
        );
        testStudentInformationEntity = TestDataUtil.createTestStudentInformationEntityA(testUserEntity);
    }

    @Test
    public void testSave() throws Exception {
        when(studentInformationRepository.save(testStudentInformationEntity)).thenReturn(testStudentInformationEntity);

        StudentInformationEntity result = studentInformationService.save(testStudentInformationEntity);

        assertEquals(result, testStudentInformationEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        when(studentInformationRepository.findAll()).thenReturn(List.of(testStudentInformationEntity));

        List<StudentInformationEntity> result = studentInformationService.findAll();

        assertEquals(result, List.of(testStudentInformationEntity));
    }

    @Test
    public void testFindOneById() throws Exception {
        when(studentInformationRepository.findById(testStudentInformationEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));

        Optional<StudentInformationEntity> result =
                studentInformationService.findOneById(testStudentInformationEntity.getId());

        assertEquals(result.get(), testStudentInformationEntity);
    }

    @Test
    public void testFindOneByStudentId() throws Exception {
        when(studentInformationRepository.findByStudentId(testUserEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));

        Optional<StudentInformationEntity> result =
                studentInformationService.findOneByStudentId(testUserEntity.getId());

        assertEquals(result.get(), testStudentInformationEntity);
    }

    @Test
    public void testExists() throws Exception {
        when(studentInformationRepository.existsById(testStudentInformationEntity.getId())).thenReturn(true);

        boolean result = studentInformationService.exists(testStudentInformationEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testPartialUpdate() throws Exception {
        StudentInformationEntity updatedStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityB(testUserEntity);

        when(studentInformationRepository.findById(testStudentInformationEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));
        when(studentInformationRepository.save(testStudentInformationEntity))
                .thenReturn(updatedStudentInformationEntity);

        StudentInformationEntity result = studentInformationService
                        .partialUpdate(testStudentInformationEntity.getId(), updatedStudentInformationEntity);

        assertEquals(result, updatedStudentInformationEntity);
    }

    @Test
    public void testPartialUpdateWhenNoStudentInformationExists() throws Exception {
        StudentInformationEntity updatedStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityB(testUserEntity);

        when(studentInformationRepository.findById(testStudentInformationEntity.getId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                studentInformationService.partialUpdate(
                        testStudentInformationEntity.getId(), updatedStudentInformationEntity)
        );
    }

    @Test
    public void testDelete() throws Exception {
        studentInformationService.delete(testStudentInformationEntity.getId());

        verify(studentInformationRepository, times(1))
                .deleteById(testStudentInformationEntity.getId());
    }

}
