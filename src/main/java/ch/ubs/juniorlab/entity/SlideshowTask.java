package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SlideshowTask")
public class SlideshowTask extends Task {

    @Column(name = "Format", length = 10)
    private String format;

    @Column(name = "FileFormat", length = 10)
    private String fileFormat;

    @Column(name = "SocialMediaPlatforms", length = 20)
    private String socialMediaPlatforms;

    @Column(name = "PhotoCount")
    private Integer photoCount;

    @Column(name = "Resolution", length = 15)
    private String resolution;

    // Konstruktoren
    public SlideshowTask() {
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

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
