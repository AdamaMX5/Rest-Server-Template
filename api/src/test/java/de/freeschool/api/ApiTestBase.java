package de.freeschool.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.freeschool.api.controller.AuthController;
import de.freeschool.api.controller.UserController;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.dto.UserDetailsDto;
import de.freeschool.api.models.request.LoginRequest;
import de.freeschool.api.models.response.LoginResponse;
import de.freeschool.api.models.response.RegisterResponse;
import de.freeschool.api.models.type.RoleType;
import de.flussmark.api.repository.*;
import de.flussmark.openapi.models.*;
import de.freeschool.api.repository.RoleRepository;
import de.freeschool.api.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApiTestBase {
    @LocalServerPort
    protected int randomServerPort;
    static private ObjectMapper mapper;


    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected AuthController authController;

    @Autowired
    protected UserController userController;


    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Value("${admin.email}")
    private String adminEmail;

    protected String apiBaseUrl = "";

    protected String jwtToken = "";

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }


    @BeforeAll
    protected void cleanupUsersAndAccounts() {
        if (userRepository == null) {
            return;
        }
        for (UserEntity user : userRepository.findAll()) {
            if (!user.getEmail().equals(adminEmail)) {
                userRepository.delete(user);
            }
        }
    }

    protected String getUrl(String path) {
        if (apiBaseUrl.equals("")) {
            apiBaseUrl = "http://localhost:" + randomServerPort + "/";
        }
        return apiBaseUrl + path;
    }

    protected String getApiUrl(String path) {
        return getUrl("api/v1/" + path);
    }


    protected void registerUser(String email, String password) {
        UserDetailsDto register = new UserDetailsDto();
        register.setEmail(email);
        register.setPassword(password);
        ResponseEntity<RegisterResponse> response = postWithoutToken("auth/register", register, RegisterResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

    protected void registerAdminUser(String email, String password) {
        registerUser(email, password);
        UserEntity user = userRepository.findByUsername(email).get();
        user.getRoles().add(roleRepository.findByName(RoleType.ADMIN).get());
        userRepository.save(user);
    }


    protected void loginUser(String email, String password) {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);
        ResponseEntity<LoginResponse> loginResponse = postWithoutToken("auth/login", login, LoginResponse.class);
        Assertions.assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        jwtToken = Objects.requireNonNull(loginResponse.getBody()).getJwtToken();
    }

    protected <T, U> ResponseEntity<T> performRequest(String endpoint, U request, Class<T> responseType,
                                                      HttpMethod method, boolean withToken) {
        try {
            URI requestUri = new URI(getApiUrl(endpoint));
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", "en");

            if (withToken) {
                headers.set("Authorization", "Bearer " + jwtToken);
            }

            HttpEntity<U> httpRequest = new HttpEntity<>(request, headers);

            return http(responseType, requestUri, method, httpRequest);
        } catch (URISyntaxException e) {
            Assertions.fail(e.toString(), e);
        }
        return null;
    }

    protected <T, U> ResponseEntity<T> performRequestWithToken(String endpoint, U request, Class<T> responseType,
                                                               HttpMethod method) {
        return performRequest(endpoint, request, responseType, method, true);
    }

    protected <T, U> ResponseEntity<T> performRequestWithoutToken(String endpoint, U request, Class<T> responseType,
                                                                  HttpMethod method) {
        return performRequest(endpoint, request, responseType, method, false);
    }

    protected <T> ResponseEntity<T> getWithToken(String endpoint, Class<T> responseType) {
        return performRequestWithToken(endpoint, null, responseType, HttpMethod.GET);
    }

    protected <T> List<T> getWithTokenAsList(String endpoint, Class<T> responseType) {
        ResponseEntity<List> response = performRequestWithToken(endpoint, null, List.class, HttpMethod.GET);
        List<?> dataList = response.getBody();
        List<T> result = new ArrayList<>();

        if (dataList != null) {
            for (Object obj : dataList) {
                result.add(responseType.cast(obj));
            }
        }
        return result;
    }

    protected <T, U> ResponseEntity<T> postWithToken(String endpoint, U request, Class<T> responseType) {
        return performRequestWithToken(endpoint, request, responseType, HttpMethod.POST);
    }

    protected <T, U> ResponseEntity<T> putWithToken(String endpoint, U request, Class<T> responseType) {
        return performRequestWithToken(endpoint, request, responseType, HttpMethod.PUT);
    }

    protected <T, U> ResponseEntity<T> deleteWithToken(String endpoint, U request, Class<T> responseType) {
        return performRequestWithToken(endpoint, request, responseType, HttpMethod.DELETE);
    }

    protected <T, U> ResponseEntity<T> postWithoutToken(String endpoint, U request, Class<T> responseType) {
        return performRequestWithoutToken(endpoint, request, responseType, HttpMethod.POST);
    }

    protected <T> ResponseEntity<T> getWithoutToken(String endpoint, Class<T> responseType) {
        return performRequestWithoutToken(endpoint, null, responseType, HttpMethod.GET);
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
                throw new HttpFailException(
                        "Response not parseable: " + e.getMessage() + "\nJSON:" + response.getBody(),
                        response.getStatusCode()
                );
            }
        } else {
            throw new HttpFailException(response.getBody(), response.getStatusCode());
        }
    }

}
