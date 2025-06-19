package ch.ubs.juniorlab;



import ch.ubs.juniorlab.Service.MailService;
import ch.ubs.juniorlab.Service.PDFService;
import ch.ubs.juniorlab.Service.TaskProcessingService;

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

        PDFService pdfService = context.getBean(PDFService.class);
        pdfService.generatePDF(taskService.getAllTasks().get(1));


    }
}


