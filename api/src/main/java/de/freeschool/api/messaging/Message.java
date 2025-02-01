package de.freeschool.api.messaging;

import lombok.Data;

@Data
public class Message {
    /**
     * Which medium to use to send the message
     */
    public enum Medium {
        EMAIL, ALL, TELEGRAM, PUSH
    }

    String subject;
    String message;
    Medium medium;

    public String getFullText() {
        StringBuilder builder = new StringBuilder();
        builder.append("[medium: ").append(medium.toString()).append("] ");
        builder.append("[subject: ").append(subject).append("]\n");
        builder.append("[message: ").append(message).append("]\n");
        return builder.toString();
    }
}
