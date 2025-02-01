package de.freeschool.api.controller;


import de.freeschool.api.exception.MessageIdException;
import de.freeschool.api.manager.UserManager;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.dto.AdminUserDetailsDto;
import de.freeschool.api.models.dto.UsersListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class AdminController extends MainController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserManager userManager;


    /**
     * PUT /admin/user/{user_id} : Updates user details
     *
     * @param userId               ID of the user (required)
     * @param adminUserDetailsData (required)
     * @return OK (status code 200)
     * or Bad input (status code 409)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "adminUserUserIdPut", summary = "Updates user details", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = AdminUserDetailsDto.class))}), @ApiResponse(responseCode =
                    "409", description = "Bad input"), @ApiResponse(responseCode = "401", description = "Unauthorized"
            )})
    @RequestMapping(method = RequestMethod.PUT, value = "/admin/user/{user_id}", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<AdminUserDetailsDto> adminUserUserIdPut(
            @Parameter(name = "user_id", description = "ID of the user", required = true, in = ParameterIn.PATH) @PathVariable("user_id") Integer userId,
            @Parameter(name = "AdminUserDetailsData", description = "", required = true) @Valid @RequestBody AdminUserDetailsDto adminUserDetailsData) {
        UserEntity userEntity = null;
        try {
            userEntity = userManager.updateUserDetails(userId, adminUserDetailsData);
        } catch (MessageIdException e) {
            responseFailMessage(e.getMessageId());
        }
        return new ResponseEntity<>(wrapToUserDetailsData(userEntity), HttpStatus.OK);
    }

    /**
     * GET /admin/users : Returns list of users
     *
     * @param page         (optional)
     * @param countPerPage (optional)
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "adminUsersGet", summary = "Returns list of users", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = UsersListDto.class))}), @ApiResponse(responseCode = "401",
                    description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.GET, value = "/admin/users", produces = {"application/json"})
    public ResponseEntity<UsersListDto> adminUsersGet(
            @Parameter(name = "page", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "page",
                    required = false) Integer page,
            @Parameter(name = "count_per_page", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value
                    = "count_per_page", required = false) Integer countPerPage) {
        UsersListDto result = new UsersListDto();
        Page<UserEntity> paged = userManager.getUsersList(page, countPerPage);
        List<AdminUserDetailsDto> users = paged.get().map(this::wrapToUserDetailsData).collect(Collectors.toList());
        result.setUsers(users);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private AdminUserDetailsDto wrapToUserDetailsData(UserEntity userEntity) {
        AdminUserDetailsDto data = new AdminUserDetailsDto();
        data.setId(userEntity.getId());
        data.setUid(userEntity.getUid());
        data.setUserDetails(userManager.getUserDetails(userEntity));
        data.setVerifyCode(userEntity.getVerifyCode());
        data.setIsVerify(userEntity.isVerify());
        data.setIsEmailNewsletter(userEntity.isEmailNewsletter());
        data.setEmailRecovery(userEntity.getEmailRecovery());
        //       data.setLastEmailChange(userEntity.getLastEmailChange().atOffset(ZoneOffset.UTC));
        data.setPasswordReset(userEntity.getPasswordReset());
        //       data.setLastPasswordReset(userEntity.getLastPasswordReset().atOffset(ZoneOffset.UTC));
        data.setRoles(userEntity.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList()));
        return data;
    }
}
