package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserRepository;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private RoleEntity testRoleEntity;

    private DepartmentEntity testDepartmentEntity;

    private UserEntity testUserEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
    }

    @Test
    public void testSave() {
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);

        UserEntity result = userService.save(testUserEntity);

        assertEquals(result, testUserEntity);
    }

    @Test
    public void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(testUserEntity));

        List<UserEntity> result = userService.findAll();

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByEmail() {
        when(userRepository.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of(testUserEntity));

        List<UserEntity> result = userService.findAllByEmail(testUserEntity.getEmail());

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByDepartmentIdAndRoleId() {
        when(userRepository.findAllByDepartmentIdAndRoleId(testDepartmentEntity.getId(), testRoleEntity.getId()))
                .thenReturn(List.of(testUserEntity));

        List<UserEntity> result =
                userService.findAllByDepartmentIdAndRoleId(testDepartmentEntity.getId(), testRoleEntity.getId());

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindAllByIdIn() {
        when(userRepository.findAllByIdIn(List.of(testUserEntity.getId()))).thenReturn(List.of(testUserEntity));

        List<UserEntity> result = userService.findAllByIdIn(List.of(testUserEntity.getId()));

        assertEquals(result, List.of(testUserEntity));
    }

    @Test
    public void testFindOneById() {
        when(userRepository.findById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));

        Optional<UserEntity> result = userService.findOneById(testUserEntity.getId());

        assertEquals(result.get(), testUserEntity);
    }

    @Test
    public void testFindOneByEmail() {
        when(userRepository.findByEmail(testUserEntity.getEmail())).thenReturn(Optional.of(testUserEntity));

        Optional<UserEntity> result = userService.findOneByEmail(testUserEntity.getEmail());

        assertEquals(result.get(), testUserEntity);
    }

    @Test
    public void testExists() {
        when(userRepository.existsById(testUserEntity.getId())).thenReturn(true);

        boolean result = userService.exists(testUserEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testPartialUpdate() {
        UserEntity updatedUserEntity = TestDataUtil.createTestUserEntityB(testRoleEntity, testDepartmentEntity);

        when(userRepository.findById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(updatedUserEntity);

        UserEntity result = userService.partialUpdate(testUserEntity.getId(), updatedUserEntity);

        assertEquals(result, updatedUserEntity);
    }

    @Test
    public void testPartialUpdateWhenNoUserExists() {
        UserEntity updatedUserEntity = TestDataUtil.createTestUserEntityB(testRoleEntity, testDepartmentEntity);

        when(userRepository.findById(testUserEntity.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.partialUpdate(testUserEntity.getId(), updatedUserEntity));
    }

    @Test
    public void testDelete() {
        userService.delete(testUserEntity.getId());

        verify(userRepository, times(1)).deleteById(testUserEntity.getId());
    }

    @Test
    public void testLoadUserByUsername() {
        when(userRepository.findByEmail(testUserEntity.getEmail())).thenReturn(Optional.of(testUserEntity));

        UserDetails result = userService.loadUserByUsername(testUserEntity.getUsername());

        assertEquals(result, testUserEntity);
    }

    @Test
    public void testLoadUserByUsernameWhenNoUserExists() {
        when(userRepository.findByEmail(testUserEntity.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(testUserEntity.getUsername()));
    }

}
