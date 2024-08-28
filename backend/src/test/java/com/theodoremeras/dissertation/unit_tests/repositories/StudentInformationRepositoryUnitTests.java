package com.theodoremeras.dissertation.unit_tests.repositories;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentRepository;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleRepository;
import com.theodoremeras.dissertation.student_information.StudentInformationEntity;
import com.theodoremeras.dissertation.student_information.StudentInformationRepository;
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
public class StudentInformationRepositoryUnitTests {

    @Autowired
    private StudentInformationRepository studentInformationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUserEntity;

    private StudentInformationEntity testStudentInformationEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects and save parent entities
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        roleRepository.save(testRoleEntity);

        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        departmentRepository.save(testDepartmentEntity);

        testUserEntity = TestDataUtil.createTestUserEntityA(
                testRoleEntity,
                testDepartmentEntity
        );
        userRepository.save(testUserEntity);

        testStudentInformationEntity = TestDataUtil.createTestStudentInformationEntityA(testUserEntity);
    }

    @Test
    public void testFindAll() {
        studentInformationRepository.save(testStudentInformationEntity);

        List<StudentInformationEntity> result = studentInformationRepository.findAll();

        assertEquals(result, List.of(testStudentInformationEntity));
    }

    @Test
    public void testFindById() {
        StudentInformationEntity savedStudentInformation =
                studentInformationRepository.save(testStudentInformationEntity);

        Optional<StudentInformationEntity> result =
                studentInformationRepository.findById(savedStudentInformation.getId());

        assertEquals(result.get(), testStudentInformationEntity);
    }

    @Test
    public void testFindByStudentId() {
        studentInformationRepository.save(testStudentInformationEntity);

        Optional<StudentInformationEntity> result =
                studentInformationRepository.findByStudentId(testUserEntity.getId());

        assertEquals(result.get(), testStudentInformationEntity);
    }

    @Test
    public void testExistsById() {
        StudentInformationEntity savedStudentInformation =
                studentInformationRepository.save(testStudentInformationEntity);

        boolean result = studentInformationRepository.existsById(savedStudentInformation.getId());

        assertTrue(result);
    }

    @Test
    public void testDeleteById() {
        StudentInformationEntity savedStudentInformation =
                studentInformationRepository.save(testStudentInformationEntity);

        studentInformationRepository.deleteById(savedStudentInformation.getId());

        assertFalse(studentInformationRepository.existsById(savedStudentInformation.getId()));
    }

}
