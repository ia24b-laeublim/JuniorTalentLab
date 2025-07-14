package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Task")
@Inheritance(strategy = InheritanceType.JOINED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "TargetAudience", length = 255)
    private String targetAudience;

    @Column(name = "BudgetChf", precision = 10, scale = 2)
    private BigDecimal budgetChf;

    @Column(name = "Deadline")
    private LocalDate deadline;

    @Column(name = "MaxFileSizeMb")
    private Integer maxFileSizeMb;

    @Column(name = "Channel", length = 50)
    private String channel;

    @Column(name = "HandoverMethod", length = 45)
    private String handoverMethod;

    @Column(name = "Status", length = 20)
    private String status;

    // ✅ FIXED: Force eager loading to ensure client data is always fetched
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "Client", nullable = false, foreignKey = @ForeignKey(name = "fk_Task_Client"))
    private Person client;

    // ✅ FIXED: Force eager loading for apprentice too
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "Apprentice", nullable = true, foreignKey = @ForeignKey(name = "fk_Task_Apprentice"))
    private Person apprentice;

    @Column(name = "Progress", nullable = true, length = 100)
    private String progress;

    public Task() {
        this.status = "open";
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Person getClient() {
        return client;
    }

    public void setClient(Person client) {
        this.client = client;
    }

    public Person getApprentice() {
        return apprentice;
    }

    public void setApprentice(Person apprentice) {
        this.apprentice = apprentice;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTaskType() {
        if (this instanceof FlyerTask) {
            return "Flyer";
        }
        if (this instanceof PosterTask) {
            return "Poster";
        }
        if (this instanceof VideoTask) {
            return "Video";
        }
        if (this instanceof SlideshowTask) {
            return "Slideshow";
        }
        if (this instanceof PollTask) {
            return "Poll";
        }
        if (this instanceof PhotoTask) {
            return "Photo";
        }
        return "General Task";
    }

    public String getSpecificRequirements() {
        StringBuilder sb = new StringBuilder();

        if (this instanceof FlyerTask flyer) {
            appendField(sb, "Paper Size", flyer.getPaperSize());
            appendField(sb, "Paper Type", flyer.getPaperType());
        } else if (this instanceof PosterTask poster) {
            appendField(sb, "Format", poster.getFormat());
            appendField(sb, "Poster Size", poster.getPosterSize());
            appendField(sb, "Paper Type", poster.getPaperType());
            appendField(sb, "Print Quality (DPI)", poster.getPrintQualityDpi() != null ? poster.getPrintQualityDpi().toString() : null);
            appendField(sb, "Mounting Type", poster.getMountingType());
        } else if (this instanceof PhotoTask photo) {
            appendField(sb, "Format", photo.getFormat());
            appendField(sb, "File Format", photo.getFileFormat());
            appendField(sb, "Platforms", photo.getSocialMediaPlatforms());
            appendField(sb, "Resolution", photo.getResolution());
        } else if (this instanceof SlideshowTask slide) {
            appendField(sb, "Format", slide.getFormat());
            appendField(sb, "File Format", slide.getFileFormat());
            appendField(sb, "Platforms", slide.getSocialMediaPlatforms());
            appendField(sb, "Resolution", slide.getResolution());
            appendField(sb, "Photo Count", slide.getPhotoCount() != null ? slide.getPhotoCount().toString() : null);
        } else if (this instanceof VideoTask video) {
            appendField(sb, "Length (sec)", video.getLengthSec() != null ? video.getLengthSec().toString() : null);
            appendField(sb, "Voiceover", video.getVoiceover() != null ? (video.getVoiceover() ? "Yes" : "No") : null);
            appendField(sb, "Disclaimer", video.getDisclaimer() != null ? (video.getDisclaimer() ? "Yes" : "No") : null);
            appendField(sb, "Branding", video.getBrandingRequirements());
            appendField(sb, "Format", video.getFormat());
            appendField(sb, "File Format", video.getFileFormat());
            appendField(sb, "Platforms", video.getSocialMediaPlatforms());
            appendField(sb, "Resolution", video.getResolution());
            appendField(sb, "Music Style", video.getMusicStyle());
        } else if (this instanceof PollTask poll) {
            appendField(sb, "Questions", poll.getQuestionCount() != null ? poll.getQuestionCount().toString() : null);
            appendField(sb, "Type", poll.getQuestionType());
            appendField(sb, "Start", poll.getStartDate() != null ? poll.getStartDate().toString() : null);
            appendField(sb, "End", poll.getEndDate() != null ? poll.getEndDate().toString() : null);
            appendField(sb, "Anonymous", poll.getAnonymous() != null ? (poll.getAnonymous() ? "Yes" : "No") : null);

        }

        return sb.length() > 0 ? sb.toString() : "No specific requirements";
    }

    private void appendField(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(label).append(": ").append(value);
        }
    }
}
