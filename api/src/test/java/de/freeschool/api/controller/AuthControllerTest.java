package de.freeschool.api.controller;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import de.freeschool.api.HttpFailException;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.repository.UserRepository;
import de.freeschool.api.models.request.LoginRequest;
import de.freeschool.api.models.response.LoginResponse;
import de.freeschool.api.models.response.RegisterResponse;
import de.freeschool.api.models.dto.UserDetailsDto;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("DataFlowIssue")
@GeneralTestSetup
class AuthControllerTest extends ApiTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void authLoginPostBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("blah");
        request.setPassword("blub");

        HttpFailException exception = assertThrows(HttpFailException.class,
                () -> postWithoutToken("auth/login", request, LoginResponse.class)
        );

        assertEquals(exception.getMessage(), "Email not found");
    }

    @Test
    @Transactional
    void authRegisterAndLogin() {
        String email = "newuserwithname@example.com";
        String password = "123456";
        UserDetailsDto register = new UserDetailsDto();
        register.setEmail(email);
        register.setPassword(password);
        register.setCompany("FlussMark");
        register.setFirstname("Max");
        register.setLastname("Mustermann");
        register.setZipcode("02376");
        ResponseEntity<RegisterResponse> response = postWithoutToken("auth/register", register, RegisterResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertThat(response.getBody().getMessage()).contains("Registration successful");

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        ResponseEntity<LoginResponse> loginResponse = authController.authLoginPost(request);
        assertEquals(HttpStatusCode.valueOf(200), loginResponse.getStatusCode());
        assertEquals(loginResponse.getBody().getFirstName(), "Max");
        assertEquals(loginResponse.getBody().getLastName(), "Mustermann");

        // Check user properties
        checkNewUserProperties(email, password);
    }

    @Test
    @Transactional
    void authRegisterExistingEmail() {
        String email = "themail@example.com";
        String password = "123456";

        // first time registration
        UserDetailsDto register = new UserDetailsDto();
        register.setEmail(email);
        register.setPassword(password);
        ResponseEntity<RegisterResponse> response = postWithoutToken("auth/register", register, RegisterResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertThat(response.getBody().getMessage()).contains("Registration successful");

        // register again
        HttpFailException exception = assertThrows(HttpFailException.class,
                () -> postWithoutToken("auth/register", register, RegisterResponse.class)
        );
        Assertions.assertThat(exception.getMessage()).contains("Email is already registered");
    }

    void checkNewUserProperties(String email, String password) {
        UserEntity user = userRepository.findByUsername(email).orElseThrow();
        Assertions.assertThat(user.getEmail()).isEqualTo(email);
        Assertions.assertThat(user.getUsername()).isEqualTo(email);
        List<SigningKey> keys = user.getKeys();
        Assertions.assertThat(keys).hasSize(1);
        Assertions.assertThat(user.getPassword()).isNotEqualTo(password);
        Assertions.assertThat(user.isUser()).isTrue();
        Assertions.assertThat(user.isAdmin()).isFalse();
    }

}