package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DepartmentControllerIntegrationTests {

    private DepartmentService departmentService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public DepartmentControllerIntegrationTests(DepartmentService departmentService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.departmentService = departmentService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateDepartment() throws Exception {
        DepartmentEntity testDepartment = TestDataUtil.createTestDepartmentEntityA();
        String departmentJson = objectMapper.writeValueAsString(testDepartment);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testDepartment.getName())
        );
    }

    @Test
    public void testGetAllDepartments() throws Exception {
        DepartmentEntity testDepartmentA = TestDataUtil.createTestDepartmentEntityA();
        DepartmentEntity savedDepartmentA = departmentService.save(testDepartmentA);
        DepartmentEntity testDepartmentB = TestDataUtil.createTestDepartmentEntityB();
        DepartmentEntity savedDepartmentB = departmentService.save(testDepartmentB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedDepartmentA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedDepartmentA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedDepartmentB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(savedDepartmentB.getName())
        );
    }

    @Test
    public void testGetDepartmentById() throws Exception {
        DepartmentEntity testDepartment = TestDataUtil.createTestDepartmentEntityA();
        DepartmentEntity savedDepartment = departmentService.save(testDepartment);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments/" + savedDepartment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedDepartment.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(savedDepartment.getName())
        );
    }

    @Test
    public void testGetDepartmentByIdWhenNoDepartmentExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        DepartmentEntity testDepartment = TestDataUtil.createTestDepartmentEntityA();
        DepartmentEntity savedDepartment = departmentService.save(testDepartment);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/departments/" + savedDepartment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteDepartmentWhenNoDepartmentExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/departments/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
