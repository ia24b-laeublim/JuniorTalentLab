package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PosterTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosterTaskRepository extends JpaRepository<PosterTask, Long> {
}
