package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PhotoTask")
public class PhotoTask extends Task {

    @Column(name = "Format", length = 10)
    private String format;

    @Column(name = "FileFormat", length = 10)
    private String fileFormat;

    @Column(name = "SocialMediaPlatforms", length = 20)
    private String socialMediaPlatforms;

    @Column(name = "Resolution", length = 15)
    private String resolution;

    // Konstruktoren
    public PhotoTask() {
    }

    public PhotoTask(String format, String fileFormat, String socialMediaPlatforms, String resolution) {
        this.format = format;
        this.fileFormat = fileFormat;
        this.socialMediaPlatforms = socialMediaPlatforms;
        this.resolution = resolution;
    }

    // Getter & Setter

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
}
