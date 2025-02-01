package de.freeschool.api.controller;


import de.freeschool.api.exception.MessageIdException;
import de.freeschool.api.exception.NoRefreshableTokenException;
import de.freeschool.api.manager.UserManager;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.dto.UserDetailsDto;
import de.freeschool.api.models.request.ForgotPasswordRequest;
import de.freeschool.api.models.request.LoginRequest;
import de.freeschool.api.models.request.ResetPasswordRequest;
import de.freeschool.api.models.request.TwoFactorAuthRequest;
import de.freeschool.api.models.response.LoginResponse;
import de.freeschool.api.models.response.MessageResponse;
import de.freeschool.api.models.response.RegisterResponse;
import de.freeschool.api.repository.RoleRepository;
import de.freeschool.api.repository.UserRepository;
import de.freeschool.api.security.JwtGenerator;
import de.freeschool.api.security.JwtTokenBlacklist;
import de.freeschool.api.telegram.TelegramApi;
import de.freeschool.api.util.DateFunctions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "auth", description = "the auth API")
public class AuthController extends MainController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private UserManager userManager;
    @Autowired
    private JwtTokenBlacklist tokenBlacklist;
    @Autowired
    private TelegramApi botApi;

    @Value("${school.url}")
    private String schoolUrl;
    @Value("${registration.isEmailVerificationNeeded}")
    private boolean isVerificationNeeded;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;


    /**
     * POST /auth/forgotPassword : Sends an Email with Reset Link.
     *
     * @param forgotPasswordRequest (required)
     * @return Email send to Email-Address (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "authForgotPasswordPost", summary = "Sends an Email with Reset Link.", responses =
            {@ApiResponse(responseCode = "200", description = "Email send to Email-Address", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = MessageResponse.class))}), @ApiResponse(responseCode = "401",
                    description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.POST, value = "/auth/forgotPassword", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<MessageResponse> authForgotPasswordPost(
            @Parameter(name = "ForgotPasswordRequest", description = "", required = true) @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        Optional<UserEntity> user = userRepository.findByUsername(forgotPasswordRequest.getEmail());
        if (user.isPresent()) {
            if (userManager.sendForgotPasswordMail(user.get(), getApiURL("resetPassword"))) {
                return ResponseEntity.ok(new MessageResponse().message(message("auth.forgotPassword")));
            }
        }
        responseFailMessage("auth.login.emailNotFound");
        return null;
    }

    /**
     * POST /auth/login : Logs in a user
     *
     * @param loginRequest (required)
     * @return Successful login (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "authLoginPost", summary = "Logs in a user", responses = {@ApiResponse(responseCode =
            "200", description = "Successful login", content = {@Content(mediaType = "application/json", schema =
    @Schema(implementation = LoginResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.POST, value = "/auth/login", produces = {"application/json"}, consumes = {
            "application/json"})
    public ResponseEntity<LoginResponse> authLoginPost(
            @Parameter(name = "LoginRequest", description = "", required = true) @Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Login with Email: " + loginRequest.getEmail());
        Optional<UserEntity> user = userRepository.findByUsername(loginRequest.getEmail());

        if (user.isEmpty()) {
            responseFailMessage("auth.login.emailNotFound");
            return null;
        }
        if (user.get().getLockedUntil() != null &&
                user.get().getLockedUntil().after(DateFunctions.instantToDate(Instant.now()))) {
            responseFailMessage("auth.login.accountLocked");
            return null;
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            if (isVerificationNeeded && !user.get().isVerify()) {
                userManager.sendVerificationMail(user.get(), getApiURL("verify"));
                responseFailMessage("auth.login.emailNotVerified");
                return null;
            }
            if (user.get().is2FA()) {
                userManager.send2FACode(user.get());
                return ResponseEntity.ok(createLoginResponseWith2FA(user.get()));
            } else {
                return ResponseEntity.ok(createLoginResponseWithToken(user.get()));
            }
        } else {
            userManager.lockUserForSecounds(user.get(), 30);
        }

        responseFailMessage("auth.login.passwordWrong");
        return null;
    }

    /**
     * POST /auth/twoFactorAuth : The Code for 2FA the user has to enter
     *
     * @param twoFactorAuthRequest (required)
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "authTwoFactorAuthPost", summary = "The Code for 2FA the user has to enter", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = LoginResponse.class))}), @ApiResponse(responseCode = "401",
                    description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.POST, value = "/auth/twoFactorAuth", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<LoginResponse> authTwoFactorAuthPost(
            @Parameter(name = "TwoFactorAuthRequest", description = "", required = true) @Valid @RequestBody TwoFactorAuthRequest twoFactorAuthRequest) {
        Optional<UserEntity> user = userRepository.findByUsername(twoFactorAuthRequest.getEmail());

        if (user.isEmpty()) {
            responseFailMessage("auth.login.emailNotFound");
            return null;
        }
        if (twoFactorAuthRequest.getTwoFactorAuthCode().equals(user.get().getTelegramCode())) {
            return ResponseEntity.ok(createLoginResponseWithToken(user.get()));
        } else {
            responseFailMessage("auth.login.twoFactorAuthCodeWrong");
            userManager.send2FACode(user.get());
            return null;
        }
    }


    /**
     * GET /auth/logout : Logs out a user
     *
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "authLogoutGet", summary = "Logs out a user", responses = {@ApiResponse(responseCode =
            "200", description = "OK"), @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.GET, value = "/auth/logout")
    public ResponseEntity<Void> authLogoutGet() {
        invalidateCurrentToken(0);
        return ResponseEntity.status(200).body(null);
    }

    /**
     * Make sure a token can't be used anymore
     */
    private void invalidateCurrentToken(int secondsUntilInvalid) {
        // Token invalidieren
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String oldJWTToken = authentication.getDetails().toString();
        // validate token
        if (StringUtils.hasText(oldJWTToken) && jwtGenerator.validateToken(oldJWTToken)) {
            tokenBlacklist.addToBlacklist(oldJWTToken, secondsUntilInvalid);
        } else {
            throw new NoRefreshableTokenException("You have no available Token to refresh");
        }
    }


    /**
     * GET /auth/refreshToken : Refreshes the JWT token
     *
     * @return OK (status code 200)
     * or Unauthorized (status code 401)
     */
    @Operation(operationId = "authRefreshTokenGet", summary = "Refreshes the JWT token", responses =
            {@ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json"
                    , schema = @Schema(implementation = LoginResponse.class))}), @ApiResponse(responseCode = "401",
                    description = "Unauthorized")})
    @RequestMapping(method = RequestMethod.GET, value = "/auth/refreshToken", produces = {"application/json"})
    public ResponseEntity<LoginResponse> authRefreshTokenGet() {
        invalidateCurrentToken(60);
        // Create response with renewed token
        return ResponseEntity.ok(createLoginResponseWithToken(getUser()));
    }

    private LoginResponse createLoginResponseWithToken(UserEntity user) {
        LoginResponse response = new LoginResponse();
        response.setJwtToken(jwtGenerator.generateToken(user));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRoles(user.getRoleNames());
        response.setTwoFactorAuthEnabled(user.getTelegramChatId() > 0);
        return response;
    }

    private LoginResponse createLoginResponseWith2FA(UserEntity user) {
        LoginResponse response = new LoginResponse();
        response.setTwoFactorAuthEnabled(user.getTelegramChatId() > 0);
        return response;
    }


    /**
     * POST /auth/register : Register a new user. Sends an Email with Verify Link.
     *
     * @param userDetailsData (required)
     * @return Successful Registration (status code 200)
     * or Problems with Data (status code 409)
     */
    @Operation(operationId = "authRegisterPost", summary = "Register a new user. Sends an Email with Verify Link.",
            responses = {@ApiResponse(responseCode = "200", description = "Successful Registration", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation =
                            RegisterResponse.class))}), @ApiResponse(responseCode = "409", description =
            "Problems " + "with Data")})
    @RequestMapping(method = RequestMethod.POST, value = "/auth/register", produces = {"application/json"}, consumes
            = {"application/json"})
    public ResponseEntity<RegisterResponse> authRegisterPost(
            @Parameter(name = "UserDetailsData", description = "", required = true) @Valid @RequestBody UserDetailsDto userDetailsData) {
        logger.info("New registration with email " + userDetailsData.getEmail());
        try {
            UserEntity user = userManager.createUser(userDetailsData);
            userManager.save(user);
            if (isVerificationNeeded) {
                userManager.sendVerificationMail(user, getApiURL("verify"));
            }
        } catch (MessageIdException e) {
            responseFailMessage(e.getMessageId());
        }
        RegisterResponse response = new RegisterResponse();
        response.setMessage(message("auth.registration"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /auth/verify : Verifies a user
     *
     * @param v     Verification Code (required)
     * @param email Email (required)
     * @return Successfully verified (status code 200)
     * or Wrong verification code (status code 401)
     */
    @Operation(operationId = "authVerifyGet", summary = "Verifies a user", responses = {@ApiResponse(responseCode =
            "200", description = "Successfully verified", content = {@Content(mediaType = "application/json", schema
            = @Schema(implementation = MessageResponse.class))}), @ApiResponse(responseCode = "401", description =
            "Wrong verification code")})
    @RequestMapping(method = RequestMethod.GET, value = "/auth/verify", produces = {"application/json"})
    public ResponseEntity<MessageResponse> authVerifyGet(
            @NotNull @Parameter(name = "v", description = "Verification Code", required = true, in =
                    ParameterIn.QUERY) @Valid @RequestParam(value = "v", required = true) String v,
            @NotNull @Parameter(name = "email", description = "Email", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "email", required = true) String email) {
        try {
            userManager.verifyUser(v, email);
            MessageResponse response = new MessageResponse();
            response.setMessage(message("auth.verify.successful"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (MessageIdException e) {
            responseFailMessage(e.getMessageId());
            return null;
        }
    }

    /**
     * POST /auth/resetPassword : Resets a user&#39;s password
     *
     * @param resetPasswordRequest (required)
     * @return New Password set (status code 200)
     * or Wrong reset code (status code 401)
     */
    @Operation(operationId = "authResetPasswordPost", summary = "Resets a user's password", responses =
            {@ApiResponse(responseCode = "200", description = "New Password set", content = {@Content(mediaType =
                    "application/json", schema = @Schema(implementation = MessageResponse.class))}),
                    @ApiResponse(responseCode = "401", description = "Wrong reset code")})
    @RequestMapping(method = RequestMethod.POST, value = "/auth/resetPassword", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<MessageResponse> authResetPasswordPost(
            @Parameter(name = "ResetPasswordRequest", description = "", required = true) @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            userManager.resetPassword(resetPasswordRequest.getCode(), resetPasswordRequest.getEmail(),
                    resetPasswordRequest.getPassword(), resetPasswordRequest.getPasswordRepeat()
            );
        } catch (MessageIdException e) {
            responseFailMessage(e.getMessageId());
        }
        MessageResponse response = new MessageResponse();
        response.setMessage(message("auth.resetPassword.successful"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
