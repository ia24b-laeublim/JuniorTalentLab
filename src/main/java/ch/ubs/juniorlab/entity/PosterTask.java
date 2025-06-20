package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PosterTask")
public class PosterTask extends Task {

    @Column(name = "Format", length = 10)
    private String format;

    @Column(name = "PosterSize", length = 20)
    private String posterSize;

    @Column(name = "PaperType", length = 50)
    private String paperType;

    @Column(name = "PrintQualityDpi")
    private Integer printQualityDpi;

    @Column(name = "MountingType", length = 50)
    private String mountingType;

    // Konstruktoren
    public PosterTask() {
    }

    // Getter & Setter

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public void setPosterSize(String posterSize) {
        this.posterSize = posterSize;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
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
}
