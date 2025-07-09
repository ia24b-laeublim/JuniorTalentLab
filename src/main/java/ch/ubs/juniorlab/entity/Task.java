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

    @Column(name = "Title", nullable = false, length = 255)
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
}