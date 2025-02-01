package de.freeschool.api.engine;

import de.freeschool.api.exception.MessageIdException;
import de.freeschool.api.manager.UserManager;
import de.freeschool.api.telegram.TelegramApi;
import de.freeschool.api.telegram.models.TelegramUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class TelegramEngine {
    private static final Logger logger = LoggerFactory.getLogger(TelegramEngine.class);

    @Autowired
    private TelegramApi botApi;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserManager userManager;

    @Value("${telegrambot.token}")
    private String token;
    @Value("${telegrambot.webhookurl}")
    private String webhookurl;

    private long lastUpdateId = -1;

    private HashMap<Long, List<String>> userTexts = new HashMap<>();


    @Scheduled(cron = "${telegrambot.scheduling.expression:0/10 * * * * *}") // execute every 10 seconds
    protected void cron() {
        trigger();
    }

    public void trigger() {
        logger.info("Triggering TelegramEngine");
        try {
            TelegramUpdate update = botApi.update(lastUpdateId + 1);
            interpretUpdates(update);
        } catch (Exception e) {
            logger.error("Error while triggering TelegramEngine", e);
        }
    }

    private void interpretUpdates(TelegramUpdate updates) {
        updates.getResult().forEach(this::interpretUpdate);
    }

    private void interpretUpdate(TelegramUpdate.Update update) {
        lastUpdateId = update.getId();

        if (update.getMessage() != null) {
            interpretMessage(update.getMessage());
        }
    }

    private void interpretMessage(TelegramUpdate.Message message) {
        long chatId = message.getFrom().getId();
        if (message.getText() != null) {
            if (message.getText().startsWith("/")) {
                interpretCommand(chatId, message.getText());
            } else {
                interpretText(chatId, message.getText());
            }
        }
    }

    private void interpretCommand(long chatId, String command) {
        logger.info("Interpreting command: " + command);
        switch (command) {
            case "/start":
                botApi.sendMessage(chatId, message("telegrambot.command.welcome"));
                break;
            case "/help":
                botApi.sendMessage(chatId, message("telegrambot.command.help"));
                break;
            default:
                botApi.sendMessage(chatId, message("telegrambot.command.unknown"));
                break;
        }
    }

    private void interpretText(long chatId, String text) {
        logger.info("Interpreting text: " + text);
        String regex = "\\b\\d{10}\\b"; // 10 digits
        String lowerText = text.toLowerCase();
        if (text.contains("@")) {
            // email
            logger.info("EmailAdresse: " + text);
        } else if (text.matches(regex)) {
            // linkUp Code
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            matcher.find();
            String linkUpCode = matcher.group();
            logger.info("Found linkUpCode: " + linkUpCode);
            try {
                userManager.linkTelegramToAccount(chatId, linkUpCode);
                botApi.sendMessage(chatId, message("telegrambot.link.successful"));
            } catch (MessageIdException e) {
                botApi.sendMessage(chatId, message(e.getMessageId()));
            }

        } else if (lowerText.contains("muh") || lowerText.contains("kuh")) {     // easteregg
            botApi.sendMessage(chatId, "\uD83D\uDC2E Muuhhhhh");
        } else if (lowerText.contains("miau") || lowerText.contains("katze")) {     // easteregg
            botApi.sendMessage(chatId, "\uD83D\uDC08 Miauuuuu");
        } else {
            logger.info("Text: " + text);
            if (userTexts.get(chatId) == null) {
                List<String> texts = new ArrayList<>();
                texts.add(text);
                userTexts.put(chatId, texts);
            } else {
                userTexts.get(chatId).add(text);
            }
            int size = userTexts.get(chatId).size();
            int random = (int) (Math.random() * size);
            botApi.sendMessage(chatId, userTexts.get(chatId).get(random));
        }
    }

    String message(String identifier) {
        // Retrieve message string. Use the identifier as fallback if it cannot be found.
        // (this behavior is used, e.g., for the maintenance mode message)
        return messageSource.getMessage(identifier, null, identifier, LocaleContextHolder.getLocale());
    }
}
