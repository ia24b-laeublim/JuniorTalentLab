package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChannel(String channel);
    List<Task> findByApprenticeId(Long apprenticeId);
    List<Task> findByDeadline(LocalDate deadline);

    // ✅ FIXED: Custom query to ensure client data and attachments are always fetched
    @Query("SELECT t FROM Task t JOIN FETCH t.client c LEFT JOIN FETCH t.apprentice a LEFT JOIN FETCH t.attachment")
    List<Task> findAllWithClients();

    // Alternative query for open tasks only
    @Query("SELECT t FROM Task t JOIN FETCH t.client c LEFT JOIN FETCH t.apprentice a LEFT JOIN FETCH t.attachment WHERE t.status IS NULL OR t.status = 'open' OR t.status = 'REJECTED'")
    List<Task> findOpenTasksWithClients();

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline <= :cutoff")
    int deleteTasksWithDeadlineOlderThan(LocalDate cutoff);

}
