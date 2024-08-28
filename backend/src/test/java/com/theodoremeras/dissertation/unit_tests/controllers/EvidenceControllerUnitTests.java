package com.theodoremeras.dissertation.unit_tests.controllers;


import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.evidence.*;
import com.theodoremeras.dissertation.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class EvidenceControllerUnitTests {

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private EcApplicationService ecApplicationService;

    @MockBean
    private EvidenceMapper evidenceMapper;

    private final MockMvc mockMvc;

    private EcApplicationEntity testEcApplicationEntity;

    private EvidenceEntity testEvidenceEntity;

    private EvidenceDto testEvidenceDto;

    @Autowired
    public EvidenceControllerUnitTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        UserEntity testUserEntity = TestDataUtil
                .createTestUserEntityA(TestDataUtil.createTestRoleEntityA(), TestDataUtil.createTestDepartmentEntityA());
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(testEcApplicationEntity);
        testEvidenceDto = TestDataUtil.createTestEvidenceDtoA(testEcApplicationEntity.getId());
    }

    @Test
    public void testUploadEvidence() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content" .getBytes());

        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));
        when(evidenceService.save(eq(multipartFile), any(EvidenceEntity.class))).thenReturn(testEvidenceEntity);
        when(evidenceMapper.mapToDto(testEvidenceEntity)).thenReturn(testEvidenceDto);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence?ecApplicationId=" + testEcApplicationEntity.getId())
                        .file(multipartFile)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(testEvidenceDto.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.fileName")
                        .value(testEvidenceDto.getFileName())
        );
    }

    @Test
    public void testUploadEvidenceWhenNoApplicationOrFileIsSpecified() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", (byte[]) null);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence")
                        .file(multipartFile)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testUploadEvidenceWhenNoApplicationExists() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content" .getBytes());

        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence?ecApplicationId=" + testEcApplicationEntity.getId())
                        .file(multipartFile)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllEvidenceByApplicationId() throws Exception {
        when(evidenceService.findAllByApplicationId(testEcApplicationEntity.getId()))
                .thenReturn(List.of(testEvidenceEntity));
        when(evidenceMapper.mapToDto(testEvidenceEntity)).thenReturn(testEvidenceDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence?ecApplicationId=" + testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testEvidenceDto.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].fileName").value(testEvidenceDto.getFileName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId").value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testServeFile() throws Exception {
        when(evidenceService.loadAsResource(testEvidenceEntity.getFileName()))
                .thenReturn(mock(Resource.class));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence/" + testEvidenceEntity.getFileName())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testServeFileWhenNoFileExists() throws Exception {
        when(evidenceService.loadAsResource(testEvidenceEntity.getFileName()))
                .thenReturn(null);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence/" + testEvidenceEntity.getFileName())
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteEvidence() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/evidence/" + testEvidenceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(evidenceService, times(1)).delete(testEvidenceEntity.getId());
    }

}
