package de.freeschool.api.controller;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import de.freeschool.api.HttpFailException;
import de.freeschool.api.messaging.Message;
import de.freeschool.api.messaging.MessageSink;
import de.freeschool.api.messaging.MessagingService;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.request.ForgotPasswordRequest;
import de.freeschool.api.models.response.MessageResponse;
import de.freeschool.api.models.request.ResetPasswordRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GeneralTestSetup
@TestPropertySource(locations = "classpath:application-integrationtest-with-verification.properties")
class AuthControllerVerificationTest extends ApiTestBase {
    @Autowired
    MessagingService messagingService;

    class TestingSink implements MessageSink {
        Message lastMessage;

        public Message getLastMessage() {
            return lastMessage;
        }

        @Override
        public boolean send(UserEntity user, Message message) {
            lastMessage = message;
            return true;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    String email = "john@example.com";

    @BeforeEach
    protected void removeUser() {
        for (UserEntity user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                userRepository.delete(user);
            }
        }
    }

    /**
     * This tests the registration and verifcation life-cycle via email
     */
    @Test
    void authRegisterUserWithVerification() throws UnsupportedEncodingException {
        registerAndVerification(false);
    }

    /**
     * This test will register a user and also do a login attempt before the verification was done.
     * This will trigger a new verification email with a new code.
     */
    @Test
    void authRegisterUserWithVerificationAfterLogInAttempt() throws UnsupportedEncodingException {
        registerAndVerification(true);
    }

    /**
     * This tests the password reset life-cycle via email
     */
    @Test
    void authForgetAndSelectNewPassword() throws UnsupportedEncodingException {
        registerAndVerification(false);
        // logout
        forgetAndSelectNewPassword();
    }

    private void forgetAndSelectNewPassword() throws UnsupportedEncodingException {
        TestingSink sink = new TestingSink();
        messagingService.setDefaultSink(sink);

        // fail login
        // loginUser(email, "456");

        String forgetEndpoint = "auth/forgotPassword";
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail(email);
        ResponseEntity<MessageResponse> result = postWithoutToken(forgetEndpoint, forgotRequest, MessageResponse.class);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody().getMessage())
                .isEqualTo("The password has been reset. Please check your email inbox to set a new password.");

        // Get Reset Code from database
        UserEntity user = userRepository.findByEmail(email).get();
        String resetCode = user.getPasswordReset();
        Date lastReset = user.getLastPasswordReset();
        Assertions.assertThat(resetCode).hasSize(32);

        // Validate message that was sent to the user
        Message lastMessage = sink.getLastMessage();
        Assertions.assertThat(lastMessage.getSubject()).isEqualTo("Neues Passwort wählen");

        // extract the verification code from the URL in the message
        // http://localhost:42811/api/v1/auth/resetPassword?code=fwb87ewd2840hfnlp4p4znsnpr1rdu2z&email=john@example.com
        String regex = "(http://.*\\?code=(.+)&email=(.+com))";
        Matcher matcher = Pattern.compile(regex).matcher(lastMessage.getMessage());
        Assertions.assertThat(matcher.find()).isTrue();

        // Validate URL components
        String url = matcher.group(1);
        String resetCodeFromUrl = matcher.group(2);
        String emailFromUrl = matcher.group(3);
        Assertions.assertThat(url).contains("/api/v1/auth/resetPassword");
        Assertions.assertThat(resetCodeFromUrl).isEqualTo(resetCode);
        Assertions.assertThat(emailFromUrl).isEqualTo(URLEncoder.encode(email, "UTF-8"));

        // call verification endpoint
        String verificationEndpoint = "auth/resetPassword";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setCode(resetCodeFromUrl);
        request.setEmail(email);
        request.setPassword("456");
        request.setPasswordRepeat("456");
        ResponseEntity<MessageResponse> resultRe = postWithoutToken(verificationEndpoint, request,
                MessageResponse.class
        );
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        String message = resultRe.getBody().getMessage();
        Assertions.assertThat(message).isEqualTo("New password successfully saved.");

        // Validate verification status in database
        user = userRepository.findByEmail(email).get();
        Assertions.assertThat(user.getPasswordReset()).isNull();

        // make sure user can log in with new password
        loginUser(email, "456");
    }

    void registerAndVerification(boolean attemptLoginBeforeVerified) throws UnsupportedEncodingException {
        TestingSink sink = new TestingSink();
        messagingService.setDefaultSink(sink);

        // act
        registerUser(email, "123");

        String initialVerifyCode = "";
        if (attemptLoginBeforeVerified) {
            UserEntity user = userRepository.findByEmail(email).get();
            initialVerifyCode = user.getVerifyCode();

            // make sure user cannot log in
            try {
                loginUser(email, "123");
            } catch (HttpFailException e) {
                Assertions.assertThat(e.getMessage()).contains("Email is not verified");
            }
        }

        // Get verification info from database
        UserEntity user = userRepository.findByEmail(email).get();
        String verifyCode = user.getVerifyCode();
        Assertions.assertThat(verifyCode).hasSize(32);
        Assertions.assertThat(user.isVerify()).isFalse();
        if (attemptLoginBeforeVerified) {
            // make sure a new verification code was generated
            Assertions.assertThat(initialVerifyCode).isNotEqualTo(verifyCode);
        }

        // Validate message that was sent to the user
        Message lastMessage = sink.getLastMessage();
        Assertions.assertThat(lastMessage.getSubject()).isEqualTo("Registrierung bestätigen");

        // extract the verification code from the URL in the message
        // http://localhost:42811/api/v1/auth/verify?v=fwb87ewd2840hfnlp4p4znsnpr1rdu2z&email=john@example.com
        String regex = "(http://.*\\?v=(.+)&email=(.+com))";
        Matcher matcher = Pattern.compile(regex).matcher(lastMessage.getMessage());
        Assertions.assertThat(matcher.find()).isTrue();

        // Validate URL components
        String url = matcher.group(1);
        String verifyCodeFromUrl = matcher.group(2);
        String emailFromUrl = matcher.group(3);
        Assertions.assertThat(url).contains("/api/v1/auth/verify");
        Assertions.assertThat(verifyCodeFromUrl).isEqualTo(verifyCode);
        Assertions.assertThat(emailFromUrl).isEqualTo(URLEncoder.encode(email, "UTF-8"));

        // call verification endpoint
        String verificationEndpoint = "auth/verify?v=" + verifyCodeFromUrl + "&email=" + emailFromUrl;
        ResponseEntity<Void> result = getWithoutToken(verificationEndpoint, Void.class);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Validate verification status in database
        user = userRepository.findByEmail(email).get();
        Assertions.assertThat(user.isVerify()).isTrue();

        // make sure user can log in
        loginUser(email, "123");
    }
}