package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.VideoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoTaskRepository extends JpaRepository<VideoTask, Long> {
    // Hier wurde ebenfalls die findByTaskId-Methode entfernt.
}