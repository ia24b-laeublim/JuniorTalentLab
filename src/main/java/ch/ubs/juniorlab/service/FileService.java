package ch.ubs.juniorlab.service;

import ch.ubs.juniorlab.entity.UploadedFile;
import ch.ubs.juniorlab.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class FileService {
    private final FileRepository repo;
    public FileService(FileRepository repo) { this.repo = repo; }

    /** Speichert eine Datei als UploadedFile in der DB */
    public UploadedFile store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        UploadedFile e = new UploadedFile();
        e.setFilename(filename);
        e.setContentType(file.getContentType());
        e.setData(file.getBytes());
        return repo.save(e);
    }

    /** Lädt eine Datei per ID */
    public UploadedFile load(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Datei nicht gefunden: " + id));
    }
}
