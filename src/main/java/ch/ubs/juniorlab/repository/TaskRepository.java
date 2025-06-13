package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChannel(String channel);
    List<Task> findByApprenticeId(Long apprenticeId);
}
