package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChannel(String channel);
    List<Task> findByApprenticeId(Long apprenticeId);

    // ✅ FIXED: Custom query to ensure client data and attachments are always fetched
    @Query("SELECT t FROM Task t JOIN FETCH t.client c LEFT JOIN FETCH t.apprentice a LEFT JOIN FETCH t.attachment")
    List<Task> findAllWithClients();

    // Alternative query for open tasks only
    @Query("SELECT t FROM Task t JOIN FETCH t.client c LEFT JOIN FETCH t.apprentice a LEFT JOIN FETCH t.attachment WHERE t.status IS NULL OR t.status = 'open' OR t.status = 'REJECTED'")
    List<Task> findOpenTasksWithClients();
}
