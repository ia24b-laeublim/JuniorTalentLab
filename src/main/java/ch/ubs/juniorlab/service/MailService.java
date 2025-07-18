package ch.ubs.juniorlab.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailService {

    private final String smtpHost = "smtp-relay.brevo.com";
    private final int smtpPort = 587;
    private final String smtpUsername = "9009c1001@smtp-brevo.com";
    private final String smtpPassword = "h3s8zfSBkpWnyJYd";
    private final String senderEmail = "madox.laeubli@gmail.com";

    public void sendEmail(String toEmail, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("E-Mail erfolgreich an " + toEmail + " gesendet!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
