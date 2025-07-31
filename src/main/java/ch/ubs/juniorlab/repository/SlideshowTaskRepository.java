package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.SlideshowTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowTaskRepository extends JpaRepository<SlideshowTask, Long> {
    // Auch hier wurde die findByTaskId-Methode entfernt.
}