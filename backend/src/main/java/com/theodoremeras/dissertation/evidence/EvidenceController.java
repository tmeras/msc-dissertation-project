package com.theodoremeras.dissertation.evidence;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EvidenceController {

    private final EvidenceService evidenceService;

    private final EcApplicationService ecApplicationService;

    private final EvidenceMapper evidenceMapper;

    public EvidenceController(
            EvidenceService evidenceService,
            EcApplicationService ecApplicationService, EvidenceMapper evidenceMapper
    ) {
        this.evidenceService = evidenceService;
        this.ecApplicationService = ecApplicationService;
        this.evidenceMapper = evidenceMapper;
    }

    @PostMapping(path = "/evidence")
    public ResponseEntity<EvidenceDto> uploadEvidence(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ecApplicationId") Integer ecApplicationId
    ) {
        // File to be uploaded and relevant EC application must be specified
        if (file.isEmpty() || file.getName().isBlank() || ecApplicationId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        EvidenceEntity evidenceEntity =
                EvidenceEntity.builder().fileName(file.getName()).build();

        // Verify that the specified EC application exists
        Optional<EcApplicationEntity> ecApplication =
                ecApplicationService.findOneById(ecApplicationId);
        if (ecApplication.isPresent())
            evidenceEntity.setEcApplication(ecApplication.get());
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        try {
            EvidenceEntity savedEvidenceEntity = evidenceService.save(file, evidenceEntity);
            return new ResponseEntity<>(evidenceMapper.mapToDto(savedEvidenceEntity), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/evidence")
    public List<EvidenceDto> getAllEvidenceByApplicationId(
            @RequestParam("ecApplicationId") Integer ecApplicationId
    ) {
        List<EvidenceEntity> evidenceEntities = evidenceService.findAllByEcApplicationId(ecApplicationId);

        return evidenceEntities.stream()
                .map(evidenceMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/evidence/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable("fileName") String fileName) {
        Resource file = evidenceService.loadAsResource(fileName);

        if (file == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping(path = "/evidence/{id}")
    public ResponseEntity<String> deleteEvidence(@PathVariable("id") Integer id) {
        evidenceService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
