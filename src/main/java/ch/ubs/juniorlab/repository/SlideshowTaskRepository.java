package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.SlideshowTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideshowTaskRepository extends JpaRepository<SlideshowTask, Long> {
}
