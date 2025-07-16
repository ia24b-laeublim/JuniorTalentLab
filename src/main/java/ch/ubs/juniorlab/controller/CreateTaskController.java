package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.*;
import ch.ubs.juniorlab.service.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.System.out;

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
    private final MailService mailService;
    private final HashService hashService;
    private final TaskRepository taskRepo;
    private final FileService fileService;


    public CreateTaskController(
            TaskRepository taskRepository,
            PersonRepository personRepository,
            FlyerTaskRepository flyerTaskRepository,
            VideoTaskRepository videoTaskRepository,
            PhotoTaskRepository photoTaskRepository,
            SlideshowTaskRepository slideshowTaskRepository,
            PosterTaskRepository posterTaskRepository,
            PollTaskRepository pollTaskRepository,
            MailService mailService,
            HashService hashService,
            TaskRepository taskRepo,
            FileService fileService
    ) {
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.flyerTaskRepository = flyerTaskRepository;
        this.videoTaskRepository = videoTaskRepository;
        this.photoTaskRepository = photoTaskRepository;
        this.slideshowTaskRepository = slideshowTaskRepository;
        this.posterTaskRepository = posterTaskRepository;
        this.pollTaskRepository = pollTaskRepository;
        this.mailService = mailService;
        this.hashService = hashService;
        this.taskRepo    = taskRepo;
        this.fileService = fileService;
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
            @RequestParam(required = false) String paperType,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            FlyerTask flyerTask = new FlyerTask();
            populateBaseTaskFields(flyerTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            flyerTask.setPaperSize(paperSize);
            flyerTask.setPaperType(paperType);
            flyerTask = flyerTaskRepository.save(flyerTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    flyerTask.setAttachment(uploadedFile);
                    flyerTaskRepository.save(flyerTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/flyer?error=true");
                }
            }

            // Send email
            sendTaskCreatedMail(flyerTask, client);

            return new ModelAndView("redirect:/requestPage");
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
            @RequestParam(required = false) String musicStyle,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);

            VideoTask videoTask = new VideoTask();
            populateBaseTaskFields(videoTask, title, description, targetAudience,
                    budgetChf, deadline, maxFileSizeMb,
                    channel, handoverMethod, client);

            videoTask.setLengthSec(lengthSec);
            videoTask.setVoiceover(voiceover);
            videoTask.setDisclaimer(disclaimer);
            videoTask.setBrandingRequirements(brandingRequirements);
            videoTask.setFormat(format);
            videoTask.setFileFormat(fileFormat);
            videoTask.setSocialMediaPlatforms(socialMediaPlatforms);
            videoTask.setResolution(resolution);
            videoTask.setMusicStyle(musicStyle);

            videoTask = videoTaskRepository.save(videoTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    videoTask.setAttachment(uploadedFile);
                    videoTask = videoTaskRepository.save(videoTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/video?error=true");
                }
            }

            sendTaskCreatedMail(videoTask, client);

            return new ModelAndView("redirect:/requestPage");
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
            @RequestParam(required = false) String resolution,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            PhotoTask photoTask = new PhotoTask();
            populateBaseTaskFields(photoTask, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            photoTask.setFormat(format);
            photoTask.setFileFormat(fileFormat);
            photoTask.setSocialMediaPlatforms(socialMediaPlatforms);
            photoTask.setResolution(resolution);
            photoTask = photoTaskRepository.save(photoTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    photoTask.setAttachment(uploadedFile);
                    photoTaskRepository.save(photoTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/photo?error=true");
                }
            }

            sendTaskCreatedMail(photoTask, client);

            return new ModelAndView("redirect:/requestPage");
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
            @RequestParam(required = false) String resolution,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        try {
            // Find or create the person
            Person client = findOrCreatePerson(gpn, name, prename, email);

            // Create the main task
            Task task = new SlideshowTask();
            populateBaseTaskFields(task, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);

            // Save the task first to get the ID
            task = taskRepository.save(task);

            // Set slideshow-specific fields
            SlideshowTask slideshowTask = (SlideshowTask) task;
            slideshowTask.setFormat(format);
            slideshowTask.setFileFormat(fileFormat);
            slideshowTask.setSocialMediaPlatforms(socialMediaPlatforms);
            slideshowTask.setPhotoCount(photoCount);
            slideshowTask.setResolution(resolution);

            // Save the slideshow task
            slideshowTask = slideshowTaskRepository.save(slideshowTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    slideshowTask.setAttachment(uploadedFile);
                    slideshowTaskRepository.save(slideshowTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    ModelAndView errorView = new ModelAndView("task/createSlideshowTask");
                    errorView.addObject("error", "Error uploading file: " + e.getMessage());
                    return errorView;
                }
            }

            // Send the email - ADD THIS LINE
            sendTaskCreatedMail(task, client);

            return new ModelAndView("redirect:/requestPage");

        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("task/createSlideshowTask");
            modelAndView.addObject("error", "Error creating slideshow task: " + e.getMessage());
            return modelAndView;
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
            @RequestParam(required = false) String mountingType,
            @RequestParam(name = "file", required = false) MultipartFile file
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
            posterTask = posterTaskRepository.save(posterTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    posterTask.setAttachment(uploadedFile);
                    posterTaskRepository.save(posterTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/poster?error=true");
                }
            }

            sendTaskCreatedMail(posterTask, client);

            return new ModelAndView("redirect:/requestPage");
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
            @RequestParam(required = false) Boolean anonymous,
            @RequestParam(name = "file", required = false) MultipartFile file
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
            pollTask = pollTaskRepository.save(pollTask);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    pollTask.setAttachment(uploadedFile);
                    pollTaskRepository.save(pollTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/poll?error=true");
                }
            }

            sendTaskCreatedMail(pollTask, client);

            return new ModelAndView("redirect:/requestPage");
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
            @RequestParam(required = false) String handoverMethod,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        try {
            Person client = findOrCreatePerson(gpn, name, prename, email);
            Task task = new Task();
            populateBaseTaskFields(task, title, description, targetAudience, budgetChf, deadline, maxFileSizeMb, channel, handoverMethod, client);
            task = taskRepository.save(task);

            // Handle file upload
            if (file != null && !file.isEmpty()) {
                try {
                    UploadedFile uploadedFile = fileService.store(file);
                    task.setAttachment(uploadedFile);
                    taskRepository.save(task);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ModelAndView("redirect:/create-task/other?error=true");
                }
            }

            sendTaskCreatedMail(task, client);

            return new ModelAndView("redirect:/requestPage");
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
        
        // Validate channel value - only allow "Internal" or "External"
        if (channel != null && (channel.equals("Internal") || channel.equals("External"))) {
            task.setChannel(channel);
        } else {
            task.setChannel("Internal"); // Default to Internal if invalid
        }
        
        task.setHandoverMethod(handoverMethod);
        task.setClient(client);
        task.setStatus("open");
    }


    private void sendTaskCreatedMail(Task task, Person client) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Task Created: " + task.getTitle();
        String message = String.format(
                """
                        Hello %s,
                        
                        Your task has been successfully created!
                        
                        If you wish to edit, delete, or review your task, here is the corresponding link:
                        %s
                        
                        Thank you for using Junior Talent Lab!
                        
                        Best regards,
                        Junior Talent Lab Team""",
                client.getPrename(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        // Also print to console for debugging
        out.println("Task created with URL: " + taskUrl);
        out.println("Email sent to: " + client.getEmail());
    }

}