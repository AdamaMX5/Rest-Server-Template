package de.freeschool.api.controller;

import de.freeschool.api.exception.MessageIdException;
import de.freeschool.api.manager.UserManager;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.dto.UserDetailsDto;
import de.freeschool.api.models.response.MessageResponse;
import de.freeschool.api.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "user", description = "the user API")
public class UserController extends MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserManager userManager;


    /**
     * GET /user/telegramLink : Returns a 9 digit String to link telegramBot for this user. For 2FA
     *
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "userTelegramLinkGet", summary = "Returns a 9 digit String to link telegramBot for this " +
            "user. For 2FA", responses = {@ApiResponse(responseCode = "200", description = "OK", content =
            {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.GET, value = "/user/telegramLink", produces = {"application/json"})
    public ResponseEntity<MessageResponse> userTelegramLinkGet() {
        UserEntity user = getUser();
        String digits = null;
        try {
            digits = userManager.generateTelegramLink(user);
        } catch (MessageIdException e) {
            responseFailMessage(message(e.getMessageId()));
        }
        MessageResponse response = new MessageResponse();
        response.setMessage(message("telegrambot.link.generated", new Object[]{digits}));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /user/update : Updates user details
     *
     * @param userDetailsData (required)
     * @return OK (status code 200)
     * or Bad input (status code 409)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "userUpdatePost", summary = "Updates user details", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = UserDetailsDto.class))}), @ApiResponse(responseCode = "409",
                    description = "Bad input"), @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.POST, value = "/user/update", produces = {"application/json"}, consumes =
            {"application/json"})
    public ResponseEntity<UserDetailsDto> userUpdatePost(
            @Parameter(name = "UserDetailsData", description = "", required = true) @Valid @RequestBody UserDetailsDto userDetailsData) {
        UserEntity user = getUser();

        try {
            userManager.updateUserDetails(user, userDetailsData);
            if (userDetailsData.getPassword() != null && !Objects.equals(userDetailsData.getPassword(), "")) {
                // Only update password if it was supplied
                userManager.updateUserPassword(user, userDetailsData.getPassword());
            }
        } catch (MessageIdException e) {
            responseFailMessage(message(e.getMessageId()));
        }

        userRepository.save(user);
        return new ResponseEntity<>(userManager.getUserDetails(user), HttpStatus.OK);
    }

    /**
     * GET /user/whoami : Gives details about the currently logged in user
     *
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "userWhoamiGet", summary = "Gives details about the currently logged in user",
            responses = {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType =
                    "application/json", schema = @Schema(implementation = UserDetailsDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.GET, value = "/user/whoami", produces = {"application/json"})
    public ResponseEntity<UserDetailsDto> userWhoamiGet() {
        UserEntity user = getUser();
        UserDetailsDto response = userManager.getUserDetails(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
