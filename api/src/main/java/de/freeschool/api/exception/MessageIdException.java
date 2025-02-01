package de.freeschool.api.exception;

public class MessageIdException extends Exception {

    private final String messageId;

    public MessageIdException(String messageId) {
        super(messageId);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
