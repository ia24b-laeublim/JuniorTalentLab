package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PhotoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoTaskRepository extends JpaRepository<PhotoTask, Long> {
    // Hier wurde ebenfalls die findByTaskId-Methode entfernt.
}