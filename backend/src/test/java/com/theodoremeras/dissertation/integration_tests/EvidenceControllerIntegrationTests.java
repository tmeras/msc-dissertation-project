package com.theodoremeras.dissertation.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.evidence.EvidenceEntity;
import com.theodoremeras.dissertation.evidence.EvidenceService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(roles={"Administrator"})
public class EvidenceControllerIntegrationTests {

    private EvidenceService evidenceService;

    private ParentCreationService parentCreationService;

    private MockMvc mockMvc;

    @Autowired
    public EvidenceControllerIntegrationTests(
            EvidenceService evidenceService, ParentCreationService parentCreationService, MockMvc mockMvc
    ) {
        this.evidenceService = evidenceService;
        this.parentCreationService = parentCreationService;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testUploadEvidence() throws Exception {
        EcApplicationEntity savedEcApplication =  parentCreationService.createEcApplicationParentEntity();

        MockMultipartFile multipartFile =
                new MockMultipartFile("file","test.txt",
                        "text/plain", "Test file content".getBytes());

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence?ecApplicationId=" + savedEcApplication.getId())
                        .file(multipartFile)

        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        );

    }

    @Test
    public void testUploadEvidenceWhenNoEcApplicationOrFileIsSpecified() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "",
                        "text/plain", (byte[]) null);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence")
                        .file(multipartFile)

        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testUploadEvidenceWhenNoEcApplicationExists() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content".getBytes());

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/evidence?ecApplicationId=1")
                        .file(multipartFile)

        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllEvidenceByEcApplicationId() throws Exception {
        EcApplicationEntity savedEcApplication =  parentCreationService.createEcApplicationParentEntity();
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content".getBytes());

        EvidenceEntity testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(savedEcApplication);
        testEvidenceEntity.setFileName(multipartFile.getOriginalFilename());
        EvidenceEntity savedEvidenceEntity = evidenceService.save(multipartFile, testEvidenceEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence?ecApplicationId=" + savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedEvidenceEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].fileName").value(savedEvidenceEntity.getFileName())
        );
    }

    @Test
    public void testServeFile() throws Exception {
        EcApplicationEntity savedEcApplication =  parentCreationService.createEcApplicationParentEntity();
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content".getBytes());

        EvidenceEntity testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(savedEcApplication);
        testEvidenceEntity.setFileName(multipartFile.getOriginalFilename());
        evidenceService.save(multipartFile, testEvidenceEntity);
        String fileName = evidenceService.findAllByEcApplicationId(savedEcApplication.getId()).get(0).getFileName();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence/" + fileName)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testServeFileWhenNoFileExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/evidence/file.txt" )
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteEvidence() throws Exception {
        EcApplicationEntity savedEcApplication =  parentCreationService.createEcApplicationParentEntity();
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content".getBytes());


        EvidenceEntity testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(savedEcApplication);
        testEvidenceEntity.setFileName(multipartFile.getOriginalFilename());
        EvidenceEntity savedEvidenceEntity = evidenceService.save(multipartFile, testEvidenceEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/evidence/" + savedEvidenceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteEvidenceWhenNoEvidenceExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/evidence/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
