package ch.ubs.juniorlab;

import ch.ubs.juniorlab.service.HashService;
import ch.ubs.juniorlab.service.TaskProcessingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class JuniorTalentLabApplication {

    public static void main(String[] args) throws IOException {

        ApplicationContext context = SpringApplication.run(JuniorTalentLabApplication.class, args);

        TaskProcessingService taskService = context.getBean(TaskProcessingService.class);

        // Methode aufrufen
        taskService.printAllTasks();

        HashService hs = context.getBean(HashService.class);

        System.out.println(hs.getInfoUrl(1));


    }
}


