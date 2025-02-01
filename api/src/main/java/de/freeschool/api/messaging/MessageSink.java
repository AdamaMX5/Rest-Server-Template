package de.freeschool.api.messaging;

import de.freeschool.api.models.UserEntity;

public interface MessageSink {

    /**
     * Deploy a message
     */
    boolean send(UserEntity user, Message message);

    /**
     * @return true if the sink can be currently used.
     */
    boolean isActive();
}
