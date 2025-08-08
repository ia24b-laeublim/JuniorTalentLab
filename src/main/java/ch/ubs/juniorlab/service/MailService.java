package ch.ubs.juniorlab.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class MailService {

    private final String smtpHost = "smtp-relay.brevo.com";
    private final int smtpPort = 587;
    private final String smtpUsername = "9009c1001@smtp-brevo.com";
    private final String smtpPassword = "h3s8zfSBkpWnyJYd";
    private final String senderEmail = "madox.laeubli@gmail.com";

    private Session session() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
    }

    /** Plain text (kept for compatibility) */
    public void sendEmail(String toEmail, String subject, String textContent) {
        try {
            Message message = new MimeMessage(session());
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(textContent);
            Transport.send(message);
            System.out.println("E-Mail (text) erfolgreich an " + toEmail + " gesendet!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /** HTML only */
    public void sendEmailHtml(String toEmail, String subject, String htmlContent) {
        sendEmail(toEmail, subject, null, htmlContent);
    }

    /** Best practice: multipart/alternative with optional plain text + HTML */
    public void sendEmail(String toEmail, String subject, String textContent, String htmlContent) {
        try {
            MimeMessage message = new MimeMessage(session());
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, StandardCharsets.UTF_8.name());

            if (htmlContent == null || htmlContent.isBlank()) {
                // fallback to text only
                message.setText(textContent != null ? textContent : "", StandardCharsets.UTF_8.name());
            } else {
                MimeMultipart alternative = new MimeMultipart("alternative");

                // plain text part (optional but recommended for deliverability)
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(textContent != null ? textContent : "HTML email", StandardCharsets.UTF_8.name());
                alternative.addBodyPart(textPart);

                // html part
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
                alternative.addBodyPart(htmlPart);

                message.setContent(alternative);
            }

            Transport.send(message);
            System.out.println("E-Mail (multipart) erfolgreich an " + toEmail + " gesendet!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
