package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.PhotoTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoTaskRepository extends JpaRepository<PhotoTask, Long> {
}
