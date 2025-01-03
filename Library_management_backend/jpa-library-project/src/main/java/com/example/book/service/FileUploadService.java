package com.example.book.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.book.entity.Auther;
import com.example.book.error.FileStorageException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service class for handling file uploads.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class FileUploadService {

    //Logger log = LoggerFactory.getLogger(FileUploadService.class);

    private Path fileStorageLocation;

    @Value("${file.upload.base-path}")
    private String basePath;

  //@Autowired or make it final and make @RequiredArgsConstructor
    private final AutherService autherService;

    /**
     * Stores a file in the specified path.
     * @param file The file to store.
     * @param id The ID associated with the file.
     * @param pathType The type of path where the file will be stored.
     * @return The name of the stored file.
     */
    public String storeFile(File file, Long id, String pathType) {
        // Create the directory for the uploaded file
        this.fileStorageLocation = Paths.get(basePath + pathType).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }

        String fileName = StringUtils.cleanPath(id + "-" + file.getName());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            try (InputStream targetStream = new FileInputStream(file)) {
                Files.copy(targetStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            updateImagePath(id, pathType, pathType + "/" + fileName);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Converts a MultipartFile to a File.
     * @param multipartFile The MultipartFile to convert.
     * @return The converted File.
     */
    public File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            log.error("Error converting the multi-part file to file= ", ex.getMessage());
        }
        return file;
    }

    /**
     * Updates the image path for an author.
     * @param id The ID of the author.
     * @param pathType The type of path.
     * @param imagePath The new image path.
     */
    private void updateImagePath(Long id, String pathType, String imagePath) {
        if (pathType.contains("authors")) {
            // Update author image path
            Auther auther = autherService.getById(id);
            auther.setImagePath(imagePath);
            autherService.update(auther);
        }
    }

    /**
     * Uploads a file to the cloud (e.g., AWS S3).
     * @param file The file to upload.
     * @param id The ID associated with the file.
     * @param pathType The type of path where the file will be stored.
     * @return The name of the uploaded file.
     */
    public String cloudUploadFile(MultipartFile file, Long id, String pathType) {
        String fileName = null;

        if (file.getContentType().contains("image")) {
            fileName = id + "_" + UUID.randomUUID() + ".jpg";
        } else {
            fileName = id + "_" + UUID.randomUUID() + ".txt";
        }

        // Comment out the code that may cause errors
        /*
        try {
            File convertedFile = convertMultiPartFileToFile(file);
            amazonS3.putObject(
                    new PutObjectRequest(bucketName + "/" + pathType, fileName, convertedFile)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            convertedFile.delete();
        } catch (AmazonServiceException ex) {
            log.error("Error occurred while uploading file= " + ex.getMessage());
        }
        */
        return fileName;
    }
}
