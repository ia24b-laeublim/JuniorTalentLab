package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PosterTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterTaskRepository extends JpaRepository<PosterTask, Long> {
    // Hier wurde ebenfalls die findByTaskId-Methode entfernt.
}