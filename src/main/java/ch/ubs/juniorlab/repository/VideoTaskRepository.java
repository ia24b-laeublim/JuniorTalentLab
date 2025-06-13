package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.VideoTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTaskRepository extends JpaRepository<VideoTask, Long> {
}
