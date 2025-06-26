package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.FlyerTask;
import ch.ubs.juniorlab.entity.VideoTask;
import ch.ubs.juniorlab.entity.Person;
import ch.ubs.juniorlab.repository.FlyerTaskRepository;
import ch.ubs.juniorlab.repository.PersonRepository;
import ch.ubs.juniorlab.repository.VideoTaskRepository;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final VideoTaskRepository videoTaskRepository;

    public CreateTaskController(PersonRepository personRepository, FlyerTaskRepository flyerTaskRepository, VideoTaskRepository videoTaskRepository) {
        this.personRepository = personRepository;
        this.flyerTaskRepository = flyerTaskRepository;
        this.videoTaskRepository = videoTaskRepository;
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

    @PostMapping("/create-task/video")
    public ModelAndView createVideoTask(
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
            @RequestParam(required = false) Integer lengthSec,
            @RequestParam(required = false) Boolean voiceover,
            @RequestParam(required = false) Boolean disclaimer,
            @RequestParam(required = false) String brandingRequirements,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String fileFormat,
            @RequestParam(required = false) String socialMediaPlatforms,
            @RequestParam(required = false) String resolution,
            @RequestParam(required = false) String musicStyle
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

            VideoTask videoTask = new VideoTask();
            videoTask.setTitle(title);
            videoTask.setDescription(description);
            videoTask.setTargetAudience(targetAudience);
            videoTask.setBudgetChf(budgetChf);
            videoTask.setDeadline(deadline);
            videoTask.setMaxFileSizeMb(maxFileSizeMb);
            videoTask.setChannel(channel);
            videoTask.setHandoverMethod(handoverMethod);
            videoTask.setClient(client);
            videoTask.setStatus("open");

            videoTask.setLengthSec(lengthSec);
            videoTask.setVoiceover(voiceover);
            videoTask.setDisclaimer(disclaimer);
            videoTask.setBrandingRequirements(brandingRequirements);
            videoTask.setFormat(format);
            videoTask.setFileFormat(fileFormat);
            videoTask.setSocialMediaPlatforms(socialMediaPlatforms);
            videoTask.setResolution(resolution);
            videoTask.setMusicStyle(musicStyle);

            videoTaskRepository.save(videoTask);
            return new ModelAndView("redirect:/");

        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/video?error=true");
        }
    }
}