package de.freeschool.api.messaging;

import de.freeschool.api.models.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used if no other message sink is active
 */
public class DefaultSink implements MessageSink {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSink.class);

    @Override
    public boolean send(UserEntity user, Message message) {
        logger.info("The following message would be delivered to user " + user.getEmail() + "\n" +
                message.getFullText().indent(4));
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
