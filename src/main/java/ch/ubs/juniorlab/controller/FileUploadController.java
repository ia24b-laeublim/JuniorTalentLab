package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.UploadedFile;
import ch.ubs.juniorlab.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;


@Controller
@RequestMapping("/files")
public class FileUploadController {

    private static final long MAX_SIZE = 250L * 1024 * 1024; // 250MB
    private final FileService fileService;

    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
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
}