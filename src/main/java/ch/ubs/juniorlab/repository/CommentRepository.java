package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Optional eine Methode, um alle Kommentare für einen Task zu finden
    List<Comment> findByTaskId(Long taskId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.task.id IN (SELECT t.id FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline <= :cutoff)")
    void deleteByTaskDeadlineBefore(LocalDate cutoff);
}