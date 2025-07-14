package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<UploadedFile, Long> {
}
