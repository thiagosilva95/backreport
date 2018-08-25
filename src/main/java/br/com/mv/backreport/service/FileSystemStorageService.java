package br.com.mv.backreport.service;

import br.com.mv.backreport.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {


    @Autowired
    private StorageProperties storageProperties;

    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, Paths.get(storageProperties.getReportDir()).resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(storageProperties.getOutputDir()).toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageProperties.getOutputDir()));
            Files.createDirectories(Paths.get(storageProperties.getReportDir()));
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    private boolean isValidFileName(String reportName) {
        return reportName.matches("[a-z]{2,}");
    }

    private boolean isValidFileExtension(MultipartFile file) {
        return file.getOriginalFilename().endsWith(".jrxml");
    }
}
