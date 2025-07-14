package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.UploadedFile;
import ch.ubs.juniorlab.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;


@RestController
public class FileUploadController {

    private static final long MAX_SIZE = 250L * 1024 * 1024; // 250MB
    private final FileService fileService;

    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/api/files/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Keine Datei ausgewählt.");
        }
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest()
                    .body("Datei zu groß: " + String.format("%.2f", file.getSize()/1024.0/1024.0) + " MB.");
        }
        UploadedFile saved = fileService.store(file);
        return ResponseEntity.ok("Datei gespeichert mit DB-ID: " + saved.getId());
    }

    @GetMapping("/api/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        try {
            UploadedFile file = fileService.load(fileId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
            headers.setContentDispositionFormData("attachment", file.getFilename());
            headers.setContentLength(file.getData().length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(file.getData());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}