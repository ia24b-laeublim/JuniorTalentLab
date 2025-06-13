package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "VideoTask")
public class VideoTask extends Task {

    @Column(name = "LengthSec")
    private Integer lengthSec;

    @Column(name = "Voiceover")
    private Boolean voiceover;

    @Column(name = "Disclaimer")
    private Boolean disclaimer;

    @Column(name = "BrandingRequirements", columnDefinition = "NVARCHAR(MAX)")
    private String brandingRequirements;

    @Column(name = "Format", length = 10)
    private String format;

    @Column(name = "FileFormat", length = 10)
    private String fileFormat;

    @Column(name = "SocialMediaPlatforms", length = 255)
    private String socialMediaPlatforms;

    @Column(name = "Resolution", length = 20)
    private String resolution;

    @Column(name = "MusicStyle", length = 45)
    private String musicStyle;

    // Konstruktoren
    public VideoTask() {
    }

    public VideoTask(Integer lengthSec, Boolean voiceover, Boolean disclaimer,
                     String brandingRequirements, String format, String fileFormat,
                     String socialMediaPlatforms, String resolution, String musicStyle) {
        this.lengthSec = lengthSec;
        this.voiceover = voiceover;
        this.disclaimer = disclaimer;
        this.brandingRequirements = brandingRequirements;
        this.format = format;
        this.fileFormat = fileFormat;
        this.socialMediaPlatforms = socialMediaPlatforms;
        this.resolution = resolution;
        this.musicStyle = musicStyle;
    }

    // Getter & Setter
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

    public String getMusicStyle() {
        return musicStyle;
    }

    public void setMusicStyle(String musicStyle) {
        this.musicStyle = musicStyle;
    }
}
