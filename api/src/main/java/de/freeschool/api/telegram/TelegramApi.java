package de.freeschool.api.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.freeschool.api.exception.MessageException;
import de.freeschool.api.telegram.models.TelegramGetme;
import de.freeschool.api.telegram.models.TelegramUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@Service
public class TelegramApi {
    private static final Logger logger = LoggerFactory.getLogger(TelegramApi.class);
    static private ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${telegrambot.token}")
    private String token;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public <T, U> ResponseEntity<T> performRequest(String endpoint, U request, Class<T> responseType,
                                                   HttpMethod method) {
        try {
            URI requestUri = new URI(getApiUrl(endpoint));
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", "en");

            HttpEntity<U> httpRequest = new HttpEntity<>(request, headers);

            return http(responseType, requestUri, method, httpRequest);
        } catch (URISyntaxException e) {
            logger.error("Error while building URI", e);
        }
        return null;
    }

    private String getApiUrl(String endpoint) {
        return "https://api.telegram.org/bot" + token + endpoint;
    }

    private <T, U> ResponseEntity<T> http(Class<T> responseType, URI requestUri, HttpMethod method,
                                          HttpEntity<U> httpRequest) {
        ResponseEntity<String> response = this.restTemplate.exchange(requestUri, method, httpRequest, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                if (response.getBody() == null) {
                    return new ResponseEntity<>(null, response.getStatusCode());
                }
                return new ResponseEntity<>(mapper.readValue(response.getBody(), responseType),
                        response.getStatusCode()
                );
            } catch (JsonProcessingException e) {
                logger.error("Error while parsing response", e);
                throw new MessageException(
                        "ResponseCode: " + response.getStatusCode() + "\nResponse not parseable: " + e.getMessage() +
                                "\nJSON:" + response.getBody());
            }
        } else {
            logger.error("Error while performing request: " + response.getStatusCode());
            throw new MessageException(
                    "Error while performing request: " + response.getStatusCode() + "\nBody:" + response.getBody());
        }
    }

    public TelegramGetme getMe() {
        //https://api.telegram.org/bot6485780235:AAGYPTGEMhqTbb5bzylsAAmJhj3NeMgdyoM/getMe
        return performRequest("/getMe", null, TelegramGetme.class, HttpMethod.GET).getBody();
    }

    public TelegramUpdate update(long nextUpdateId) {
        if (nextUpdateId == 0) {
            return performRequest("/getUpdates", null, TelegramUpdate.class, HttpMethod.GET).getBody();
        } else {
            return performRequest("/getUpdates?offset=" + nextUpdateId, null, TelegramUpdate.class,
                    HttpMethod.GET
            ).getBody();
        }
    }

    public void sendMessage(long chatId, String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        //https://api.telegram.org/bot<token>/sendMessage?chat_id=<chat_id>&text=<text>
        String endpoint = "/sendMessage?chat_id=" + chatId + "&text=" + text;
        performRequest(endpoint, null, Object.class, HttpMethod.GET);
    }

}
