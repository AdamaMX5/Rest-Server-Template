package de.freeschool.api.messaging;

import de.freeschool.api.models.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSink implements MessageSink {
    private static final Logger logger = LoggerFactory.getLogger(EmailSink.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Override
    public boolean send(UserEntity user, Message message) {
        return sendEmail(user.getEmail(), message.getSubject(), message.getMessage());
    }

    @Override
    public boolean isActive() {
        return !smtpHost.strip().equals("");
    }

    /**
     * Sends an email to an address. Caution: The return code does not indicate
     * if the mail could be delivered successfully!
     */
    public boolean sendEmail(String to, String subject, String text) {
        logger.debug("Sending email to " + to + " with subject " + subject);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setFrom("info@flussmark.de");
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
            return true;
        } catch (MailSendException e) {
            // TODO: Email to Admin
            // TODO: write exception in ErrorFile
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

//        try {
//            HtmlEmail email = new HtmlEmail();
//            email.setHostName("sandbox.smtp.mailtrap.io");
//            email.setSmtpPort(587);
//            email.setAuthentication("98568fe1714d83", "df7c1b1eaeb073");
////            email.setStartTLSEnabled(true);
//            email.addTo("k.ostwald90@gmail.com");
//            email.setFrom("from@example.com");
//            email.setSubject(subject);
////            email.setHtmlMsg(text);
//            email.setMsg(text);
//            email.send();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
    }
}

