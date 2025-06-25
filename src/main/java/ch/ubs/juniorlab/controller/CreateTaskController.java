package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.FlyerTask;
import ch.ubs.juniorlab.entity.Person;
import ch.ubs.juniorlab.repository.FlyerTaskRepository;
import ch.ubs.juniorlab.repository.PersonRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
public class CreateTaskController {

    private final PersonRepository personRepository;
    private final FlyerTaskRepository flyerTaskRepository;

    public CreateTaskController(PersonRepository personRepository, FlyerTaskRepository flyerTaskRepository) {
        this.personRepository = personRepository;
        this.flyerTaskRepository = flyerTaskRepository;
    }

    @PostMapping("/create-task/flyer")
    public ModelAndView createFlyerTask(
            @RequestParam String gpn,
            @RequestParam String name,
            @RequestParam(required = false) String prename,
            @RequestParam String email,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String targetAudience,
            @RequestParam(required = false) BigDecimal budgetChf,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadline,
            @RequestParam(required = false) Integer maxFileSizeMb,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String handoverMethod,
            @RequestParam(required = false) String paperSize,
            @RequestParam(required = false) String paperType
    ) {
        try {

            Person client = personRepository.findByGpn(gpn)
                    .orElseGet(() -> {
                        Person newPerson = new Person();
                        newPerson.setGpn(gpn);
                        newPerson.setName(name);
                        newPerson.setPrename(prename);
                        newPerson.setEmail(email);
                        return personRepository.save(newPerson);
                    });

            FlyerTask flyerTask = new FlyerTask();
            flyerTask.setTitle(title);
            flyerTask.setDescription(description);
            flyerTask.setTargetAudience(targetAudience);
            flyerTask.setBudgetChf(budgetChf);
            flyerTask.setDeadline(deadline);
            flyerTask.setMaxFileSizeMb(maxFileSizeMb);
            flyerTask.setChannel(channel);
            flyerTask.setHandoverMethod(handoverMethod);
            flyerTask.setClient(client);
            flyerTask.setStatus("open");
            flyerTask.setPaperSize(paperSize);
            flyerTask.setPaperType(paperType);

            FlyerTask savedTask = flyerTaskRepository.save(flyerTask);


            return new ModelAndView("redirect:/");

        } catch (Exception e) {
            System.err.println("Error creating task:");
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/flyer?error=true");
        }
    }
}