package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.conf.StorageProperties;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.evidence.EvidenceEntity;
import com.theodoremeras.dissertation.evidence.EvidenceRepository;
import com.theodoremeras.dissertation.evidence.EvidenceService;
import com.theodoremeras.dissertation.user.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvidenceServiceUnitTests {

    @Mock
    private EvidenceRepository evidenceRepository;

    @Mock
    private StorageProperties storageProperties;

    private EvidenceService evidenceService;

    private EcApplicationEntity testEcApplicationEntity;

    private EvidenceEntity testEvidenceEntity;

    @BeforeAll
    public static void createDirectory() throws IOException {
        // Create directory where evidence will be uploaded,
        // if not already created
        Files.createDirectories(Paths.get("uploaded-evidence"));
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        String uploadLocation = "uploaded-evidence";
        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(
                TestDataUtil.createTestRoleEntityA(),
                TestDataUtil.createTestDepartmentEntityA()
        );
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testEvidenceEntity = TestDataUtil.createTestEvidenceEntityA(testEcApplicationEntity);
        when(storageProperties.getLocation()).thenReturn(uploadLocation);

        // Manually instantiate EvidenceService with the mocked dependencies
        evidenceService = new EvidenceService(evidenceRepository, storageProperties);
    }

    @Test
    public void testSaveLoadDelete() throws IOException {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt",
                        "text/plain", "Test file content" .getBytes());

        // Save file
        when(evidenceRepository.save(testEvidenceEntity)).thenReturn(testEvidenceEntity);

        EvidenceEntity saveResult = evidenceService.save(multipartFile, testEvidenceEntity);

        assertEquals(saveResult, testEvidenceEntity);

        // Load file as path
        Path loadResult = evidenceService.load(saveResult.getFileName());

        assertInstanceOf(Path.class, loadResult);

        // Load file as resource
        Resource loadResourceResult = evidenceService.loadAsResource(saveResult.getFileName());

        assertInstanceOf(Resource.class, loadResourceResult);

        // Delete file
        when(evidenceRepository.findById(testEvidenceEntity.getId())).thenReturn(Optional.of(testEvidenceEntity));

        evidenceService.delete(testEvidenceEntity.getId());

        verify(evidenceRepository, times(1)).deleteById(testEvidenceEntity.getId());
    }

    @Test
    public void testDeleteWhenNoFileExists() {
        when(evidenceRepository.findById(testEvidenceEntity.getId())).thenReturn(Optional.empty());

        evidenceService.delete(testEvidenceEntity.getId());

        verify(evidenceRepository, times(1)).deleteById(testEvidenceEntity.getId());
    }

    @Test
    public void testFindAllByApplicationId() {
        when(evidenceRepository.findAllByEcApplicationId(testEcApplicationEntity.getId()))
                .thenReturn(List.of(testEvidenceEntity));

        List<EvidenceEntity> result = evidenceService.findAllByApplicationId(testEcApplicationEntity.getId());

        assertEquals(result, List.of(testEvidenceEntity));
    }

}
