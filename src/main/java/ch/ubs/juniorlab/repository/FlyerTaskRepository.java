package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.FlyerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlyerTaskRepository extends JpaRepository<FlyerTask, Long> {
    // Die findByTaskId-Methode wurde entfernt, da das Feld taskId nicht existiert.
    // Stattdessen wird die von JpaRepository geerbte Methode findById(Long id) verwendet.
}