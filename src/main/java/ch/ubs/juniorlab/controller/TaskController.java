package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // Offene Tasks (unverändert)
    @GetMapping("/open")
    public List<Task> getOpenTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == null || "REJECTED".equalsIgnoreCase(task.getStatus()))
                .toList();
    }

    // 🔹 Akzeptierte, aber NICHT erledigte Tasks anzeigen
    // MODIFIZIERT: Filtert jetzt "Finished" Tasks heraus
    @GetMapping("/accepted")
    public List<Task> getAcceptedTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> "ACCEPTED".equalsIgnoreCase(task.getStatus()) && !"Finished".equalsIgnoreCase(task.getProgress()))
                .toList();
    }

    // ⭐ NEUER ENDPUNKT: Zeigt nur erledigte Tasks an
    @GetMapping("/finished")
    public List<Task> getFinishedTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> "Finished".equalsIgnoreCase(task.getProgress()))
                .toList();
    }

    // Task akzeptieren (unverändert)
    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("ACCEPTED");
        if (task.getProgress() == null) {
            task.setProgress("Started");
        }
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // Task ablehnen (unverändert)
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("REJECTED");
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // Task-Status aktualisieren (unverändert, funktioniert für beide Seiten)
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setProgress(request.getStatus());
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }
}