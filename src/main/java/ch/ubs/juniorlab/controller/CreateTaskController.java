package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
public class CreateTaskController {

    private final TaskRepository taskRepository;
    private final PersonRepository personRepository;
    private final FlyerTaskRepository flyerTaskRepository;
    private final VideoTaskRepository videoTaskRepository;
    private final PhotoTaskRepository photoTaskRepository;
    private final SlideshowTaskRepository slideshowTaskRepository;
    private final PosterTaskRepository posterTaskRepository;
    private final PollTaskRepository pollTaskRepository;

    public CreateTaskController(
            TaskRepository taskRepository,
            PersonRepository personRepository,
            FlyerTaskRepository flyerTaskRepository,
            VideoTaskRepository videoTaskRepository,
            PhotoTaskRepository photoTaskRepository,
            SlideshowTaskRepository slideshowTaskRepository,
            PosterTaskRepository posterTaskRepository,
            PollTaskRepository pollTaskRepository
    ) {
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.flyerTaskRepository = flyerTaskRepository;
        this.videoTaskRepository = videoTaskRepository;
        this.photoTaskRepository = photoTaskRepository;
        this.slideshowTaskRepository = slideshowTaskRepository;
        this.posterTaskRepository = posterTaskRepository;
        this.pollTaskRepository = pollTaskRepository;
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
            Person client = findOrCreatePerson(gpn, name, prename, email);
            FlyerTask flyerTask = new FlyerTask();
            populateBaseTaskFields(flyerTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            flyerTask.setPaperSize(paperSize);
            flyerTask.setPaperType(paperType);
            flyerTaskRepository.save(flyerTask);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
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
            Person client = findOrCreatePerson(gpn, name, prename, email);
            VideoTask videoTask = new VideoTask();
            populateBaseTaskFields(videoTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
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

    @PostMapping("/create-task/photo")
    public ModelAndView createPhotoTask(
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
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String fileFormat,
            @RequestParam(required = false) String socialMediaPlatforms,
            @RequestParam(required = false) String resolution
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            PhotoTask photoTask = new PhotoTask();
            populateBaseTaskFields(photoTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            photoTask.setFormat(format);
            photoTask.setFileFormat(fileFormat);
            photoTask.setSocialMediaPlatforms(socialMediaPlatforms);
            photoTask.setResolution(resolution);
            photoTaskRepository.save(photoTask);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/photo?error=true");
        }
    }

    @PostMapping("/create-task/slideshow")
    public ModelAndView createSlideshowTask(
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
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String fileFormat,
            @RequestParam(required = false) String socialMediaPlatforms,
            @RequestParam(required = false) Integer photoCount,
            @RequestParam(required = false) String resolution
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            SlideshowTask slideshowTask = new SlideshowTask();
            populateBaseTaskFields(slideshowTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            slideshowTask.setFormat(format);
            slideshowTask.setFileFormat(fileFormat);
            slideshowTask.setSocialMediaPlatforms(socialMediaPlatforms);
            slideshowTask.setPhotoCount(photoCount);
            slideshowTask.setResolution(resolution);
            slideshowTaskRepository.save(slideshowTask);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/slideshow?error=true");
        }
    }

    @PostMapping("/create-task/poster")
    public ModelAndView createPosterTask(
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
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String posterSize,
            @RequestParam(required = false) String paperType,
            @RequestParam(required = false) Integer printQualityDpi,
            @RequestParam(required = false) String mountingType
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            PosterTask posterTask = new PosterTask();
            populateBaseTaskFields(posterTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            posterTask.setFormat(format);
            posterTask.setPosterSize(posterSize);
            posterTask.setPaperType(paperType);
            posterTask.setPrintQualityDpi(printQualityDpi);
            posterTask.setMountingType(mountingType);
            posterTaskRepository.save(posterTask);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/poster?error=true");
        }
    }

    @PostMapping("/create-task/poll")
    public ModelAndView createPollTask(
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
            @RequestParam(required = false) Integer questionCount,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Boolean anonymous
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            PollTask pollTask = new PollTask();
            populateBaseTaskFields(pollTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            pollTask.setQuestionCount(questionCount);
            pollTask.setQuestionType(questionType);
            pollTask.setStartDate(startDate);
            pollTask.setEndDate(endDate);
            pollTask.setAnonymous(anonymous);
            pollTaskRepository.save(pollTask);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/poll?error=true");
        }
    }

    @PostMapping("/create-task/other")
    public ModelAndView createOtherTask(
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
            @RequestParam(required = false) String handoverMethod
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            Task task = new Task();
            populateBaseTaskFields(task, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            taskRepository.save(task); // ✅ This line
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/create-task/other?error=true");
        }
    }

    // === Shared Logic ===

    private Person findOrCreatePerson(String gpn, String name, String prename, String email) {
        return personRepository.findByGpn(gpn)
                .orElseGet(() -> {
                    Person newPerson = new Person();
                    newPerson.setGpn(gpn);
                    newPerson.setName(name);
                    newPerson.setPrename(prename);
                    newPerson.setEmail(email);
                    return personRepository.save(newPerson);
                });
    }

    private void populateBaseTaskFields(
            Task task,
            String title,
            String description,
            String targetAudience,
            BigDecimal budgetChf,
            LocalDate deadline,
            Integer maxFileSizeMb,
            String channel,
            String handoverMethod,
            Person client
    ) {
        task.setTitle(title);
        task.setDescription(description);
        task.setTargetAudience(targetAudience);
        task.setBudgetChf(budgetChf);
        task.setDeadline(deadline);
        task.setMaxFileSizeMb(maxFileSizeMb);
        task.setChannel(channel);
        task.setHandoverMethod(handoverMethod);
        task.setClient(client);
        task.setStatus("open");
    }
}