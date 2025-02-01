package de.freeschool.api.controller;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import de.freeschool.api.models.dto.AdminUserDetailsDto;
import de.flussmark.openapi.models.MaintenanceModeData;
import de.freeschool.api.models.response.MetaInfoResponse;
import de.freeschool.api.models.dto.UsersListDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@GeneralTestSetup
class AdminControllerTest extends ApiTestBase {

    private final String email = "admin@example.com";
    private final String password = "pw";

    @BeforeAll
    void setUpClass() {
        // admin user is already registered as per application-integrationtest.properties
        loginUser(email, password);
    }

    @Test
    void adminMaintenance() {
        // initally disabled
        ResponseEntity<MaintenanceModeData> response = getWithToken("admin/maintenance", MaintenanceModeData.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getEnabled()).isFalse();
        Assertions.assertThat(response.getBody().getMessage()).isEmpty();

        // enable
        MaintenanceModeData mmd = new MaintenanceModeData();
        mmd.setEnabled(true);
        mmd.setMessage("it is on");
        putWithToken("admin/maintenance", mmd, Void.class);

        response = getWithToken("admin/maintenance", MaintenanceModeData.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getEnabled()).isTrue();
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("it is on");

        ResponseEntity<MetaInfoResponse> metaResponse = getWithoutToken("meta/info", MetaInfoResponse.class);
        Assertions.assertThat(metaResponse.getBody().getMaintenanceMode().getEnabled()).isTrue();
        Assertions.assertThat(metaResponse.getBody().getMaintenanceMode().getMessage()).isEqualTo("it is on");

        // disable
        mmd.setEnabled(false);
        mmd.setMessage("does not matter");
        putWithToken("admin/maintenance", mmd, Void.class);

        response = getWithToken("admin/maintenance", MaintenanceModeData.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getEnabled()).isFalse();
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("does not matter");

        metaResponse = getWithoutToken("meta/info", MetaInfoResponse.class);
        Assertions.assertThat(metaResponse.getBody().getMaintenanceMode().getEnabled()).isFalse();
        Assertions.assertThat(metaResponse.getBody().getMaintenanceMode().getMessage()).isEqualTo("");


    }

    @Test
    void adminUsersGet() {
        registerUser("u1@example.com", "123");
        registerUser("u2@example.com", "123");
        registerUser("u3@example.com", "123");
        registerUser("u4@example.com", "123");
        registerUser("u5@example.com", "123");
        registerUser("u6@example.com", "123");
        ResponseEntity<UsersListDto> response = getWithToken("admin/users?page=0&count_per_page=2", UsersListDto.class);

        // admin user only
        Assertions.assertThat(response.getBody().getUsers()).hasSize(2);

        AdminUserDetailsDto adminDetails = response.getBody().getUsers().get(0);
        Assertions.assertThat(adminDetails.getUserDetails().getEmail()).isEqualTo(email);

        // to be continued ...

    }
}