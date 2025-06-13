package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PollTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollTaskRepository extends JpaRepository<PollTask, Long> {
}
