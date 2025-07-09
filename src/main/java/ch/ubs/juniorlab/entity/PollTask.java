package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PollTask")
public class PollTask extends Task {

    @Column(name = "QuestionCount")
    private Integer questionCount;

    @Column(name = "QuestionType", length = 45)
    private String questionType;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Anonymous")
    private Boolean anonymous;

    // Konstruktoren
    public PollTask() {
    }


    // Getter & Setter

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
}
