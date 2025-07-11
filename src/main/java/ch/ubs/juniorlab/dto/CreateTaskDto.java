package ch.ubs.juniorlab.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTaskDto {

    // Basic Task fields
    private String title;
    private String description;
    private String targetAudience;
    private BigDecimal budgetChf;
    private LocalDate deadline;
    private Integer maxFileSizeMb;
    private String channel;
    private String handoverMethod;
    private String taskType; // To determine which specific task type this is
    
    // Person related fields (IDs or GPNs)
    private Long clientId;
    private String clientGpn;
    private Long apprenticeId;
    private String apprenticeGpn;
    
    // FlyerTask specific fields
    private String paperSize;
    private String paperType;
    
    // PhotoTask specific fields
    private String format;
    private String fileFormat;
    private String socialMediaPlatforms;
    private String resolution;
    
    // PollTask specific fields
    private String pollTitle;
    private String pollDescription;
    private Integer questionCount;
    private String questionType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean anonymous;
    private String distributionMethod;
    
    // PosterTask specific fields
    private String posterSize;
    private Integer printQualityDpi;
    private String mountingType;
    
    // SlideshowTask specific fields
    private Integer photoCount;
    
    // VideoTask specific fields
    private Integer lengthSec;
    private Boolean voiceover;
    private Boolean disclaimer;
    private String brandingRequirements;
    private String musicStyle;
    
    // Additional fields for form handling
    private String otherRequirements;
    
    // Default constructor
    public CreateTaskDto() {
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public BigDecimal getBudgetChf() {
        return budgetChf;
    }

    public void setBudgetChf(BigDecimal budgetChf) {
        this.budgetChf = budgetChf;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(Integer maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getHandoverMethod() {
        return handoverMethod;
    }

    public void setHandoverMethod(String handoverMethod) {
        this.handoverMethod = handoverMethod;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientGpn() {
        return clientGpn;
    }

    public void setClientGpn(String clientGpn) {
        this.clientGpn = clientGpn;
    }

    public Long getApprenticeId() {
        return apprenticeId;
    }

    public void setApprenticeId(Long apprenticeId) {
        this.apprenticeId = apprenticeId;
    }

    public String getApprenticeGpn() {
        return apprenticeGpn;
    }

    public void setApprenticeGpn(String apprenticeGpn) {
        this.apprenticeGpn = apprenticeGpn;
    }

    // FlyerTask getters/setters
    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    // PhotoTask getters/setters
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getSocialMediaPlatforms() {
        return socialMediaPlatforms;
    }

    public void setSocialMediaPlatforms(String socialMediaPlatforms) {
        this.socialMediaPlatforms = socialMediaPlatforms;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    // PollTask getters/setters
    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public String getPollDescription() {
        return pollDescription;
    }

    public void setPollDescription(String pollDescription) {
        this.pollDescription = pollDescription;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public void setDistributionMethod(String distributionMethod) {
        this.distributionMethod = distributionMethod;
    }

    // PosterTask getters/setters
    public String getPosterSize() {
        return posterSize;
    }

    public void setPosterSize(String posterSize) {
        this.posterSize = posterSize;
    }

    public Integer getPrintQualityDpi() {
        return printQualityDpi;
    }

    public void setPrintQualityDpi(Integer printQualityDpi) {
        this.printQualityDpi = printQualityDpi;
    }

    public String getMountingType() {
        return mountingType;
    }

    public void setMountingType(String mountingType) {
        this.mountingType = mountingType;
    }

    // SlideshowTask getters/setters
    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    // VideoTask getters/setters
    public Integer getLengthSec() {
        return lengthSec;
    }

    public void setLengthSec(Integer lengthSec) {
        this.lengthSec = lengthSec;
    }

    public Boolean getVoiceover() {
        return voiceover;
    }

    public void setVoiceover(Boolean voiceover) {
        this.voiceover = voiceover;
    }

    public Boolean getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(Boolean disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getBrandingRequirements() {
        return brandingRequirements;
    }

    public void setBrandingRequirements(String brandingRequirements) {
        this.brandingRequirements = brandingRequirements;
    }

    public String getMusicStyle() {
        return musicStyle;
    }

    public void setMusicStyle(String musicStyle) {
        this.musicStyle = musicStyle;
    }

    // Additional form fields
    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
    }
}