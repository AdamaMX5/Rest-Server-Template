package de.freeschool.api.telegram.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramUpdate {
    private boolean ok;
    private List<Update> result;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @JsonProperty("update_id")
        private long id;
        private Message message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private FromUser from;
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FromUser {
        private long id;
        @JsonProperty("is_bot")
        private boolean isBot;
        private String first_name;
        private String last_name;
        private String username;
        private String language_code;
    }
}
