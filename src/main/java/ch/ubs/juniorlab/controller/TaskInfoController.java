package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.service.HashService;
import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TaskInfoController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private HashService hashService;

    @GetMapping("/overview/{hash}")
    public String getTaskPage(@PathVariable String hash, Model model) {
        List<Task> allTasks = taskRepository.findAll();
        for (Task task : allTasks) {
            String taskHash = hashService.hashId(task.getId());
            if (taskHash.equals(hash)) {
                String specificRequirements = task.getSpecificRequirements();
                model.addAttribute("task", task);
                model.addAttribute("specificRequirements", specificRequirements);
                return "taskPage";
            }
        }
        return "error";
    }
}
