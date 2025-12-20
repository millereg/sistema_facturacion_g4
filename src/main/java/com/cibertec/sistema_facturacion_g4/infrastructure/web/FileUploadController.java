package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "Archivos", description = "Gestión de carga de archivos")
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    @Operation(summary = "Subir archivo")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {
        
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("El archivo está vacío");
            }

            Path uploadPath = Paths.get(uploadDir, type);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/files/" + type + "/" + filename;
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", filename);
            response.put("originalFilename", originalFilename);
            
            log.info("Archivo subido exitosamente: {}", fileUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Error al subir archivo", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al subir archivo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{type}/{filename:.+}")
    @Operation(summary = "Descargar archivo")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String type,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadDir, type).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al descargar archivo", e);
            return ResponseEntity.notFound().build();
        }
    }
}
