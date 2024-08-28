package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.role.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class RoleControllerUnitTests {

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleMapper roleMapper;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private RoleEntity testRoleEntity;

    private RoleDto testRoleDto;

    @Autowired
    public RoleControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
        testRoleDto = TestDataUtil.createTestRoleDtoA();
    }

    @Test
    public void testCreateRole() throws Exception {
        String roleJson = objectMapper.writeValueAsString(testRoleDto);

        when(roleMapper.mapFromDto(any())).thenReturn(testRoleEntity);
        when(roleService.save(testRoleEntity)).thenReturn(testRoleEntity);
        when(roleMapper.mapToDto(testRoleEntity)).thenReturn(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testRoleDto.getName())
        );
    }

    @Test
    public void testGetAllRoles() throws Exception {
        when(roleService.findAll()).thenReturn(List.of(testRoleEntity));
        when(roleMapper.mapToDto(testRoleEntity)).thenReturn(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testRoleEntity.getName())
        );
    }

    @Test
    public void testGetAllRolesByRoleName() throws Exception {
        when(roleService.findAllByRoleName(testRoleEntity.getName())).thenReturn(List.of(testRoleEntity));
        when(roleMapper.mapToDto(testRoleEntity)).thenReturn(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/roles?name=" + testRoleEntity.getName())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testRoleEntity.getName())
        );
    }

    @Test
    public void testDeleteRole() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/roles/" + testRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(roleService, times(1)).delete(testRoleEntity.getId());
    }

}
