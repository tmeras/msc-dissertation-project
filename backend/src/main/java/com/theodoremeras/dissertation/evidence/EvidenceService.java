package com.theodoremeras.dissertation.evidence;

import com.theodoremeras.dissertation.conf.StorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;

    // Path to directory where files will be uploaded
    private final Path uploadLocation;

    public EvidenceService(EvidenceRepository evidenceRepository, StorageProperties properties) {
        this.evidenceRepository = evidenceRepository;
        this.uploadLocation = Paths.get(properties.getLocation());
    }

    public EvidenceEntity save(MultipartFile file, EvidenceEntity evidenceEntity) throws IOException {
        String newFileName = (LocalDateTime.now() + file.getOriginalFilename()).replace(":", "_");
        evidenceEntity.setFileName(newFileName);

        Path destinationFile =
                this.uploadLocation.resolve(Paths.get(newFileName))
                        .normalize().toAbsolutePath();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return evidenceRepository.save(evidenceEntity);
    }

    public Path load(String filename) {
        return uploadLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(uploadLocation.toFile());
    }

    public void init() throws IOException {
        Files.createDirectories(uploadLocation);
    }

    public List<EvidenceEntity> findAllByEcApplicationId(Integer ecApplicationId) {
        return evidenceRepository.findAllByEcApplicationId(ecApplicationId);
    }

    public void delete(Integer id) {
        Optional<EvidenceEntity> foundEntity = evidenceRepository.findById(id);
        try {
            if (foundEntity.isPresent()) {
                String fileName = foundEntity.get().getFileName();
                Files.delete(load(fileName));
            }
        } catch (IOException ignored) {
            // Do not perform any action if file doesn't exist
            ignored.printStackTrace();
        }

        evidenceRepository.deleteById(id);
    }

}
