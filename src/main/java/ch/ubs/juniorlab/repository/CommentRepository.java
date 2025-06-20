package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Optional eine Methode, um alle Kommentare für einen Task zu finden
    List<Comment> findByTaskId(Long taskId);
}