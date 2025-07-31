package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PollTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollTaskRepository extends JpaRepository<PollTask, Long> {
    // Auch hier wurde die findByTaskId-Methode entfernt.
}