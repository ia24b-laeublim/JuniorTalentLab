package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    /*
    // 🔹 Alle offenen Tasks anzeigen
    @GetMapping("/open")
    public List<Task> getOpenTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == null || task.getStatus() == TaskStatus.OPEN)
                .toList();
    }
    */

    @GetMapping("/open")
    public List<Task> getOpenTasks() {
        return taskRepository.findAll().stream()
                .filter(task ->
                        task.getStatus() == null || task.getStatus() == TaskStatus.REJECTED)
                .toList();
    }


    // 🔹 Einen Task akzeptieren
    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("Accepted");
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // 🔹 Einen Task ablehnen
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("Rejected");
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accepted")
    public List<Task> getAcceptedTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == TaskStatus.ACCEPTED)
                .toList();
    }


}
