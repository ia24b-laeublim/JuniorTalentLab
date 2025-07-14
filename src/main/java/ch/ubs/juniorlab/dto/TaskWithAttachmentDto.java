package ch.ubs.juniorlab.dto;

import ch.ubs.juniorlab.entity.*;

import java.math.BigDecimal;

import java.time.LocalDate;

public class TaskWithAttachmentDto {

    // Basic Task fields

    private Long id;

    private String title;

    private String description;

    private String targetAudience;

    private BigDecimal budgetChf;

    private LocalDate deadline;

    private Integer maxFileSizeMb;

    private String channel;

    private String handoverMethod;

    private String status;

    private String progress;

    private Person client;

    private Person apprentice;

    // Type-specific fields

    private String paperSize;

    private String paperType;

    private Integer lengthSec;

    private Boolean voiceover;

    private Boolean disclaimer;

    private String brandingRequirements;

    private String format;

    private String fileFormat;

    private String socialMediaPlatforms;

    private String resolution;

    private String musicStyle;

    private Integer questionCount;

    private String questionType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean anonymous;

    private String posterSize;

    private Integer printQualityDpi;

    private String mountingType;

    private Integer photoCount;

    // Attachment info

    private AttachmentInfoDto attachment;

    public TaskWithAttachmentDto(Task task) {

        // Copy basic fields

        this.id               = task.getId();

        this.title            = task.getTitle();

        this.description      = task.getDescription();

        this.targetAudience   = task.getTargetAudience();

        this.budgetChf        = task.getBudgetChf();

        this.deadline         = task.getDeadline();

        this.maxFileSizeMb    = task.getMaxFileSizeMb();

        this.channel          = task.getChannel();

        this.handoverMethod   = task.getHandoverMethod();

        this.status           = task.getStatus();

        this.progress         = task.getProgress();

        this.client           = task.getClient();

        this.apprentice       = task.getApprentice();

        // Copy type-specific fields

        if (task instanceof FlyerTask) {

            FlyerTask flyer = (FlyerTask) task;

            this.paperSize = flyer.getPaperSize();

            this.paperType = flyer.getPaperType();

        } else if (task instanceof VideoTask) {

            VideoTask video = (VideoTask) task;

            this.lengthSec           = video.getLengthSec();

            this.voiceover           = video.getVoiceover();

            this.disclaimer          = video.getDisclaimer();

            this.brandingRequirements = video.getBrandingRequirements();

            this.format              = video.getFormat();

            this.fileFormat          = video.getFileFormat();

            this.socialMediaPlatforms = video.getSocialMediaPlatforms();

            this.resolution          = video.getResolution();

            this.musicStyle          = video.getMusicStyle();

        } else if (task instanceof PhotoTask) {

            PhotoTask photo = (PhotoTask) task;

            this.format              = photo.getFormat();

            this.fileFormat          = photo.getFileFormat();

            this.socialMediaPlatforms = photo.getSocialMediaPlatforms();

            this.resolution          = photo.getResolution();

        } else if (task instanceof PollTask) {

            PollTask poll = (PollTask) task;

            this.questionCount = poll.getQuestionCount();

            this.questionType  = poll.getQuestionType();

            this.startDate     = poll.getStartDate();

            this.endDate       = poll.getEndDate();

            this.anonymous     = poll.getAnonymous();

        } else if (task instanceof PosterTask) {

            PosterTask poster = (PosterTask) task;

            this.format         = poster.getFormat();

            this.posterSize     = poster.getPosterSize();

            this.paperType      = poster.getPaperType();

            this.printQualityDpi = poster.getPrintQualityDpi();

            this.mountingType   = poster.getMountingType();

        } else if (task instanceof SlideshowTask) {

            SlideshowTask slideshow = (SlideshowTask) task;

            this.format              = slideshow.getFormat();

            this.fileFormat          = slideshow.getFileFormat();

            this.socialMediaPlatforms = slideshow.getSocialMediaPlatforms();

            this.resolution          = slideshow.getResolution();

            this.photoCount          = slideshow.getPhotoCount();

        }

        // Copy attachment info

        if (task.getAttachment() != null) {

            this.attachment = new AttachmentInfoDto(

                    task.getAttachment().getId(),

                    task.getAttachment().getFilename(),

                    task.getAttachment().getContentType()

            );

        }

    }

    // Getters for all fields

    public Long getId() {

        return id;

    }

    public String getTitle() {

        return title;

    }

    public String getDescription() {

        return description;

    }

    public String getTargetAudience() {

        return targetAudience;

    }

    public BigDecimal getBudgetChf() {

        return budgetChf;

    }

    public LocalDate getDeadline() {

        return deadline;

    }

    public Integer getMaxFileSizeMb() {

        return maxFileSizeMb;

    }

    public String getChannel() {

        return channel;

    }

    public String getHandoverMethod() {

        return handoverMethod;

    }

    public String getStatus() {

        return status;

    }

    public String getProgress() {

        return progress;

    }

    public Person getClient() {

        return client;

    }

    public Person getApprentice() {

        return apprentice;

    }

    public String getPaperSize() {

        return paperSize;

    }

    public String getPaperType() {

        return paperType;

    }

    public Integer getLengthSec() {

        return lengthSec;

    }

    public Boolean getVoiceover() {

        return voiceover;

    }

    public Boolean getDisclaimer() {

        return disclaimer;

    }

    public String getBrandingRequirements() {

        return brandingRequirements;

    }

    public String getFormat() {

        return format;

    }

    public String getFileFormat() {

        return fileFormat;

    }

    public String getSocialMediaPlatforms() {

        return socialMediaPlatforms;

    }

    public String getResolution() {

        return resolution;

    }

    public String getMusicStyle() {

        return musicStyle;

    }

    public Integer getQuestionCount() {

        return questionCount;

    }

    public String getQuestionType() {

        return questionType;

    }

    public LocalDate getStartDate() {

        return startDate;

    }

    public LocalDate getEndDate() {

        return endDate;

    }

    public Boolean getAnonymous() {

        return anonymous;

    }

    public String getPosterSize() {

        return posterSize;

    }

    public Integer getPrintQualityDpi() {

        return printQualityDpi;

    }

    public String getMountingType() {

        return mountingType;

    }

    public Integer getPhotoCount() {

        return photoCount;

    }

    public AttachmentInfoDto getAttachment() {

        return attachment;

    }

    public static class AttachmentInfoDto {

        private Long id;

        private String filename;

        private String contentType;

        public AttachmentInfoDto(Long id, String filename, String contentType) {

            this.id          = id;

            this.filename    = filename;

            this.contentType = contentType;

        }

        public Long getId() {

            return id;

        }

        public String getFilename() {

            return filename;

        }

        public String getContentType() {

            return contentType;

        }

    }

}

