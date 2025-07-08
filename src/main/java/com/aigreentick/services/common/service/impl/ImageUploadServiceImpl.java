package com.aigreentick.services.common.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadServiceImpl {

    // Directory path where uploaded images will be stored (configured in application.properties)
    @Value("${image.upload.path}")
    private String uploadDir;

    // Base URL for accessing images (e.g., https://domain.com/images/)
    @Value("${image.base.url}")
    private String baseUrl;

    /**
     * Uploads an image to the server.
     *
     * @param file the uploaded image file
     * @return a ResponseEntity containing the image URL or error message
     * @throws Exception if any file operation fails
     */
    public ResponseEntity<?> uploadImage(MultipartFile file) throws Exception {
        // Generate a unique filename to avoid collisions (UUID + original filename)
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Resolve the complete path to where the file will be stored
        Path filePath = Paths.get(uploadDir).resolve(fileName);

        // Ensure the directory exists (creates if not)
        Files.createDirectories(filePath.getParent());

        // Copy the file input stream to the target location, replacing if already exists
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the full URL where the image can be accessed
        return ResponseEntity.ok(baseUrl + fileName);
    }

    /**
     * Serves an image from the server by its filename.
     *
     * @param filename the name of the file to serve
     * @return a ResponseEntity with the file as a Resource and its appropriate content type
     * @throws Exception if the file cannot be accessed
     */
    public ResponseEntity<Resource> serveImage(String filename) throws Exception {
        // Build the normalized path to the file
        Path path = Paths.get(uploadDir).resolve(filename).normalize();

        // Create a Resource object from the file path
        Resource resource = new UrlResource(path.toUri());

        // Return 404 if the file doesn't exist
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Determine the file's content type (e.g., image/jpeg, image/png) or default to binary stream
        MediaType contentType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // Return the file as a downloadable/previewable resource
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
