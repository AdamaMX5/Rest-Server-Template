package de.freeschool.api.controller;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import de.freeschool.api.models.response.MetaInfoResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("DataFlowIssue")
@GeneralTestSetup
class MetaControllerTest extends ApiTestBase {

    @Test
    void metaInfoGet() {
        ResponseEntity<MetaInfoResponse> response = getWithoutToken("meta/info", MetaInfoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getSchoolName()).isEqualTo("TestMark Bank");
        Assertions.assertThat(response.getBody().getSchoolUrl()).isEqualTo("testmark.de");
        Assertions.assertThat(response.getBody().getTagline()).isEqualTo("Die TestMark ist zum testen da");
        Assertions.assertThat(response.getBody().getCurrencyName()).isEqualTo("TestMark");
        Assertions.assertThat(response.getBody().getCurrencyShort()).isEqualTo("TM");
        Assertions.assertThat(response.getBody().getMaintenanceMode().getMessage()).isEqualTo("");
        Assertions.assertThat(response.getBody().getMaintenanceMode().getEnabled()).isEqualTo(false);
    }
}