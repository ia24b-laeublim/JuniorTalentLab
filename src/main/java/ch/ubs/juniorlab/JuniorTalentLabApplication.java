package ch.ubs.juniorlab;

import ch.ubs.juniorlab.service.TaskProcessingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JuniorTalentLabApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(JuniorTalentLabApplication.class, args);

    }
}
