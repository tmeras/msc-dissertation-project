package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleRepository;
import com.theodoremeras.dissertation.role.RoleService;
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
public class RoleServiceUnitTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private RoleEntity testRoleEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
    }

    @Test
    public void testSave() throws Exception {
        when(roleRepository.save(testRoleEntity)).thenReturn(testRoleEntity);

        RoleEntity result = roleService.save(testRoleEntity);

        assertEquals(result, testRoleEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        when(roleRepository.findAll()).thenReturn(List.of(testRoleEntity));

        List<RoleEntity> result = roleService.findAll();

        assertEquals(result, List.of(testRoleEntity));
    }

    @Test
    public void testFindAllByRoleName() throws Exception {
        when(roleRepository.findAllByName(testRoleEntity.getName())).thenReturn(List.of(testRoleEntity));

        List<RoleEntity> result = roleService.findAllByRoleName(testRoleEntity.getName());

        assertEquals(result, List.of(testRoleEntity));
    }

    @Test
    public void testFindOneById() throws Exception {
        when(roleRepository.findById(testRoleEntity.getId())).thenReturn(Optional.of(testRoleEntity));

        Optional<RoleEntity> result = roleService.findOneById(testRoleEntity.getId());

        assertEquals(result.get(), testRoleEntity);
    }

    @Test
    public void testExists() throws Exception {
        when(roleRepository.existsById(testRoleEntity.getId())).thenReturn(true);

        boolean result = roleService.exists(testRoleEntity.getId());

        assertTrue(result);
    }

    @Test
    public void testDelete() throws Exception {
        roleService.delete(testRoleEntity.getId());

        verify(roleRepository, times(1)).deleteById(testRoleEntity.getId());
    }

}
