package de.freeschool.api.controller;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import de.freeschool.api.models.dto.UserDetailsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("DataFlowIssue")
@GeneralTestSetup
class UserControllerTest extends ApiTestBase {

    private final String email = "user@UserControllerTest.com";
    private final String password = "123456";

    @BeforeAll
    void setUpClass() {
        registerUser(email, password);
        loginUser(email, password);
    }

    @Test
    void userUpdatePost() {

        UserDetailsDto request = new UserDetailsDto();
        request.setEmail("my@new.email");
        request.setFirstname("Newfirst");
        request.setLastname("Newlast");

        ResponseEntity<UserDetailsDto> response = postWithToken("user/update", request, UserDetailsDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getEmail()).isEqualTo("my@new.email");
        Assertions.assertThat(response.getBody().getFirstname()).isEqualTo("Newfirst");
        Assertions.assertThat(response.getBody().getLastname()).isEqualTo("Newlast");

        // Must login with new email
        loginUser("my@new.email", password);

        // Verify Whoami contains the updated data
        ResponseEntity<UserDetailsDto> whoamiResponse = getWithToken("user/whoami", UserDetailsDto.class);
        Assertions.assertThat(whoamiResponse).isEqualTo(response);
        Assertions.assertThat(response.getBody().getEmail()).isEqualTo("my@new.email");
        Assertions.assertThat(response.getBody().getFirstname()).isEqualTo("Newfirst");
        Assertions.assertThat(response.getBody().getLastname()).isEqualTo("Newlast");
    }

    @Test
    void userWhoamiGet() {
        ResponseEntity<UserDetailsDto> response = getWithToken("user/whoami", UserDetailsDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }
}