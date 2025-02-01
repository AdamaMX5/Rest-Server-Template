package de.freeschool.api.controller;

import de.freeschool.api.models.response.MetaInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "meta", description = "the meta API")
public class MetaController extends MainController {

    @Value("${school.name}")
    private String schoolName;

    @Value("${school.url}")
    private String schoolUrl;


    @RequestMapping(value = "/meta/info", method = GET, produces = "application/json")
    @ResponseBody
    @Operation(operationId = "metaInfoGet", summary = "Meta information about this instance", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MetaInfoResponse.class))}), @ApiResponse(responseCode = "400"
                    , description = "Internal error")})
    public ResponseEntity<MetaInfoResponse> metaInfoGet() {
        MetaInfoResponse response = new MetaInfoResponse();
        response.setSchoolName(schoolName);
        response.setSchoolUrl(schoolUrl);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
