package ch.ubs.juniorlab.service;

import ch.ubs.juniorlab.entity.*;
import jakarta.persistence.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {

    @PersistenceContext
    private EntityManager entityManager;

    public void generatePDF(Task task) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);

        float yStart = 750;
        float margin = 50;
        float rightColX = 320;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // === Title Bar ===
        cs.setNonStrokingColor(245, 245, 245); // Light gray background
        cs.addRect(0, yStart - 20, page.getMediaBox().getWidth(), 30);
        cs.fill();

        cs.beginText();
        cs.setNonStrokingColor(0, 0, 0); // Black text
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
        cs.newLineAtOffset(margin, yStart - 10);
        cs.showText("PDF Task #" + task.getId());
        cs.endText();

        // Left Column (General Info & Specs)
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.setNonStrokingColor(0, 0, 0);
        cs.setLeading(16f);
        cs.newLineAtOffset(margin, yStart - 60);

        underlineSection(cs, "General Information");
        writeField(cs, "Title", task.getTitle());
        writeField(cs, "Description", task.getDescription());
        writeField(cs, "Target Audience", task.getTargetAudience());
        writeField(cs, "Budget (CHF)", task.getBudgetChf() != null ? task.getBudgetChf().toString() : null);
        writeField(cs, "Deadline", task.getDeadline() != null ? task.getDeadline().format(dateFormatter) : null);
        writeField(cs, "Channel", task.getChannel());
        writeField(cs, "Status", task.getStatus());
        writeField(cs, "Progress", task.getProgress());
        writeField(cs, "Handover Method", task.getHandoverMethod());

        cs.newLine();
        underlineSection(cs, "Specifications");

        if (task instanceof FlyerTask flyer) {
            writeField(cs, "Type", "Flyer");
            writeField(cs, "Paper Size", flyer.getPaperSize());
            writeField(cs, "Paper Type", flyer.getPaperType());
        } else if (task instanceof PosterTask poster) {
            writeField(cs, "Type", "Poster");
            writeField(cs, "Format", poster.getFormat());
            writeField(cs, "Poster Size", poster.getPosterSize());
            writeField(cs, "Paper Type", poster.getPaperType());
            writeField(cs, "Print Quality (DPI)", poster.getPrintQualityDpi() != null ? poster.getPrintQualityDpi().toString() : null);
            writeField(cs, "Mounting Type", poster.getMountingType());
        } else if (task instanceof PhotoTask photo) {
            writeField(cs, "Type", "Photo");
            writeField(cs, "Format", photo.getFormat());
            writeField(cs, "File Format", photo.getFileFormat());
            writeField(cs, "Platforms", photo.getSocialMediaPlatforms());
            writeField(cs, "Resolution", photo.getResolution());
        } else if (task instanceof SlideshowTask slide) {
            writeField(cs, "Type", "Slideshow");
            writeField(cs, "Format", slide.getFormat());
            writeField(cs, "File Format", slide.getFileFormat());
            writeField(cs, "Platforms", slide.getSocialMediaPlatforms());
            writeField(cs, "Resolution", slide.getResolution());
            writeField(cs, "Photo Count", slide.getPhotoCount() != null ? slide.getPhotoCount().toString() : null);
        } else if (task instanceof VideoTask video) {
            writeField(cs, "Type", "Video");
            writeField(cs, "Length (sec)", video.getLengthSec() != null ? video.getLengthSec().toString() : null);
            writeField(cs, "Voiceover", video.getVoiceover() != null ? video.getVoiceover().toString() : null);
            writeField(cs, "Disclaimer", video.getDisclaimer() != null ? video.getDisclaimer().toString() : null);
            writeField(cs, "Branding", video.getBrandingRequirements());
            writeField(cs, "Format", video.getFormat());
            writeField(cs, "File Format", video.getFileFormat());
            writeField(cs, "Platforms", video.getSocialMediaPlatforms());
            writeField(cs, "Resolution", video.getResolution());
            writeField(cs, "Music Style", video.getMusicStyle());
        } else {
            writeField(cs, "Type", "General");
        }
        cs.endText();

        // Right Column (Client & Apprentice)
        float yRightStart = yStart - 60;
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.setLeading(16f);
        cs.newLineAtOffset(rightColX, yRightStart);

        if (task.getClient() != null) {
            underlineSection(cs, "Client");
            writePerson(cs, task.getClient());
        }

        cs.newLine();
        if (task.getApprentice() != null) {
            underlineSection(cs, "Apprentice");
            writePerson(cs, task.getApprentice());
        }

        cs.endText();

        // Footer
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
        cs.setNonStrokingColor(100, 100, 100);
        cs.newLineAtOffset(margin, 40);
        cs.showText("JuniorTalentLab");
        cs.endText();

        cs.beginText();
        cs.newLineAtOffset(page.getMediaBox().getWidth() - margin - 150, 40);
        cs.showText("Generated at: " + timeFormatter.format(java.time.LocalDateTime.now()));
        cs.endText();

        cs.close();

        File dir = new File("src/Files");
        if (!dir.exists()) dir.mkdirs();

        File pdfFile = new File(dir, "Task_" + task.getId() + ".pdf");
        document.save(pdfFile);
        document.close();

        System.out.println("✅ PDF saved at: " + pdfFile.getAbsolutePath());
    }

    private void writeField(PDPageContentStream cs, String label, String value) throws IOException {
        if (value == null) return;
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.showText(label + ": ");
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.showText(value);
        cs.newLine();
    }

    private void underlineSection(PDPageContentStream cs, String title) throws IOException {
        cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
        cs.showText(title);
        cs.newLine();
    }

    private void writePerson(PDPageContentStream cs, Person p) throws IOException {
        writeField(cs, "First Name", p.getPrename());
        writeField(cs, "Last Name", p.getName());
        writeField(cs, "E-Mail", p.getEmail());
        writeField(cs, "GPN", p.getGpn());
    }

    public File checkPDF(Task task) throws IOException {
        String folderPath = "src/Files";
        File dir = new File(folderPath);
        if (!dir.exists()) dir.mkdirs();

        File pdfFile = new File(dir, "Task_" + task.getId() + ".pdf");

        if (pdfFile.exists()) {
            System.out.println("ℹ️ PDF already exists: " + pdfFile.getAbsolutePath());
            return pdfFile;
        }

        generatePDF(task);
        return pdfFile;
    }

    public File checkPDF(Long taskId) throws IOException {
        Task task = entityManager.find(Task.class, taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task with ID " + taskId + " not found.");
        }
        return checkPDF(task);
    }
}