package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "FlyerTask")
public class FlyerTask extends Task {

    @Column(name = "PaperSize", length = 20)
    private String paperSize;

    @Column(name = "PaperType", length = 50)
    private String paperType;

    // Konstruktoren
    public FlyerTask() {
    }

    public FlyerTask(String paperSize, String paperType) {
        this.paperSize = paperSize;
        this.paperType = paperType;
    }

    // Getter & Setter

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
}
