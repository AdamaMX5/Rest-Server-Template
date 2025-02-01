package de.freeschool.api.messaging;

import de.freeschool.api.models.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central entry point for sending messages to users.
 */
@Service
public class MessagingService {

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    @Autowired
    EmailSink emailService;

    MessageSink defaultSink = new DefaultSink();

    private List<MessageSink> getMessageSinks(Message.Medium medium) {
        List<MessageSink> sinks = (switch (medium) {
            case EMAIL, ALL -> Collections.singletonList(emailService);
            case TELEGRAM, PUSH -> new ArrayList<>();  // not yet implemented
        });
        sinks = sinks.stream().filter(MessageSink::isActive).collect(Collectors.toList());
        if (sinks.isEmpty()) {
            // Fallback to default sink if no matches found
            return Collections.singletonList(defaultSink);
        }
        return sinks;
    }

    public void setDefaultSink(MessageSink sink) {
        defaultSink = sink;
    }

    /**
     * Send a message to the user using the means as specified in the
     * medium field of the message.
     *
     * @param user
     * @param message
     * @return true if the message could be delivered.
     */
    public boolean send(UserEntity user, Message message) {
        List<MessageSink> sinks = getMessageSinks(message.getMedium());
        if (sinks.stream().allMatch(sink -> sink.send(user, message))) {
            return true;
        }
        logger.error("The following message could not be delivered:\n" + message.getFullText().indent(4));
        return false;
    }

    public boolean sendVerificationMail(UserEntity user, String verificationCode,
                                        String verificationUrl) throws UnsupportedEncodingException {

        String fullName = user.getFullName();
        EmailBuilder msg = new EmailBuilder();

        msg.appendHeadline("Registrierung bestätigen", 1);
        if (fullName != null) {
            msg.append("Danke dir ").append(fullName).append(",<br><br>");
        } else {
            msg.append("Danke dir,<br><br>");
        }
        msg.append(
                "Wir freuen uns dich als Geldverbesserer begrüßen zu dürfen. Mit deiner Registration hilfst du uns " +
                        "Firmen überzeugen zu können, die FlussMark zu akzeptieren. Dann hast auch du wieder die " +
                        "Möglichkeit mehr Ausgaben mit der FlussMark zu decken.<br><br>");
        msg.append("Um deine Email-Adresse zu verifizieren klicke bitte auf folgenden Link:<br>");
        msg.append("<a href=\"" + verificationUrl + "?v=");
        msg.append(URLEncoder.encode(verificationCode, "UTF-8"))
                .append("&email=")
                .append(URLEncoder.encode(user.getEmail(), "UTF-8"));
        msg.append("\" target=\"_blank\" style=\"background:#ff9248; border-radius:8px; line-height:1em; \">Zum " +
                "Bestätigen, bitte hier klicken</a><br><br>");
        msg.append("Dein nächster Schritt ist ein Menschen-Konto zu eröffnen und ein paar Grüße an zufällige " +
                "Geldverbesserer zu überweisen<br><br>");
        msg.append("Vielen Dank für alles und liebe Grüße<br><br>");
        msg.append("Euer FlussMark-Team");

        Message message = new Message();
        message.setMedium(Message.Medium.EMAIL);
        message.setSubject("Registrierung bestätigen");
        message.setMessage(msg.toString());

        return send(user, message);
    }

    public boolean sendForgotPasswordMail(UserEntity user, String resetCode,
                                          String passwordResetUrl) throws UnsupportedEncodingException {
        String fullName = user.getFullName();
        EmailBuilder msg = new EmailBuilder();

        msg.appendHeadline("Neues Passwort wählen", 1);
        if (fullName != null) {
            msg.append("Hallo ").append(fullName).append(",<br><br>");
        } else {
            msg.append("Hallo,<br><br>");
        }
        msg.append(
                "Hast du dein Passwort vergessen? Das ist kein Problem. Mit dem untenstehenden Link kannst du dir ein" +
                        " neues Passwort geben.<br><br>");
        msg.append("solltest du nicht dein Passwort vergessen haben, dann ignoriere die Email einfach. " +
                "Dein bisheriges Passwort funktioniert noch weiterhin.<br><br>");
        msg.append("<a href=\"" + passwordResetUrl + "?code=");
        msg.append(URLEncoder.encode(resetCode, "UTF-8"))
                .append("&email=")
                .append(URLEncoder.encode(user.getEmail(), "UTF-8"));
        msg.append("\" target=\"_blank\" style=\"background:#ff9248; border-radius:8px; line-height:1em; \">" +
                "Neues Passwort festlegen" + "</a><br><br>");

        msg.append("Vielen Dank für alles und liebe Grüße<br><br>");
        msg.append("Euer FlussMark-Team");

        Message message = new Message();
        message.setMedium(Message.Medium.EMAIL);
        message.setSubject("Neues Passwort wählen");
        message.setMessage(msg.toString());

        return send(user, message);
    }
}
