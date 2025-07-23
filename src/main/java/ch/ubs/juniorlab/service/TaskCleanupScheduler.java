package ch.ubs.juniorlab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskCleanupScheduler {
    @Autowired
    private TaskProcessingService taskService;

    // Mail DeadlineReminder wird um 07:00 Uhr versendet
    @Scheduled(cron = "0 0 7 * * ?")
    public void sendDeadlineReminders() {
        taskService.sendDeadlineReminderMails();
    }

    // Einmal täglich um 03:00 Uhr um überfällige Tasks zu löschen
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldTasks() {
        taskService.deleteTasksPastDeadline(7); // 7 Tage nach Deadline
    }
}
