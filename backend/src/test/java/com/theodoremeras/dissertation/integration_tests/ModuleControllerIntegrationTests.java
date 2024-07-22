package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.module.ModuleDto;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
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
public class ModuleControllerIntegrationTests {

    private ModuleService moduleService;

    private DepartmentService departmentService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public ModuleControllerIntegrationTests(ModuleService moduleService, DepartmentService departmentService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.moduleService = moduleService;
        this.departmentService = departmentService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateModule() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoA(savedDepartment.getId());
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleDto.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartment.getId())
        );
    }

    @Test
    public void testCreateModuleWhenModuleExists() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoA(savedDepartment.getId());
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);
        moduleService.save(TestDataUtil.createTestModuleEntityA(savedDepartment));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testCreateModuleWhenNoDepartmentIsSpecified() throws Exception {
        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoA(null);
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateModuleWhenDepartmentIsNotFound() throws Exception {
        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoA(5);
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllModules() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleEntity testModuleEntityA = TestDataUtil.createTestModuleEntityA(savedDepartment);
        moduleService.save(testModuleEntityA);
        ModuleEntity testModuleEntityB = TestDataUtil.createTestModuleEntityB(savedDepartment);
        moduleService.save(testModuleEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].code").value(testModuleEntityA.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testModuleEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].code").value(testModuleEntityB.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(testModuleEntityB.getName())
        );
    }

    @Test
    public void testGetModule() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(savedDepartment);
        moduleService.save(testModuleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartment.getId())
        );
    }

    @Test
    public void testGetModuleWhenNoModuleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateModule() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(savedDepartment);
        ModuleEntity savedModuleEntity = moduleService.save(testModuleEntity);

        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoB(null);
        String moduleUpdateJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/" + savedModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(savedModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartment.getId())
        );
    }

    @Test
    public void testPartialUpdateModuleWhenNoModuleExists() throws Exception {
        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoB(1);
        String moduleUpdateJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteModule() throws Exception {
        DepartmentEntity savedDepartment = departmentService.save(TestDataUtil.createTestDepartmentEntityA());

        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(savedDepartment);
        moduleService.save(testModuleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }


    @Test
    public void testDeleteModuleWhenNoModuleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
