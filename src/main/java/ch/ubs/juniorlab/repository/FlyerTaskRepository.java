package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.FlyerTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlyerTaskRepository extends JpaRepository<FlyerTask, Long> {
}
