package de.freeschool.api.manager;

import de.freeschool.api.exception.MessageIdException;
import de.freeschool.api.messaging.MessagingService;
import de.freeschool.api.models.Role;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.type.RoleType;
import de.freeschool.api.repository.RoleRepository;
import de.freeschool.api.repository.UserRepository;
import de.freeschool.api.telegram.TelegramApi;
import de.freeschool.api.util.DateFunctions;
import de.freeschool.api.util.PasswordEncryption;
import de.freeschool.api.util.RandomNumberGenerator;
import de.freeschool.api.models.dto.AdminUserDetailsDto;
import de.freeschool.api.models.dto.UserDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.*;

@Component
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private TelegramApi botApi;

    @Value("${school.url}")
    private String schoolUrl;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${registration.ensureStrongPassword}")
    private boolean ensureStrongPassword;

    public void setupDefaultUsers() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        if (userRepository.findByUsername(adminEmail).isEmpty()) {
            UserEntity admin = UserEntity.create(schoolUrl, adminEmail, adminEmail,
                    passwordEncoder.encode(RandomNumberGenerator.generateRandomString(20)),
                    roleRepository.findByName(RoleType.ADMIN).orElseThrow()
            );
            admin.setUsername(adminEmail);
            admin.setFirstName("Admin");
            admin.setLastName("FlussMark");
            admin.setCity("");
            admin.setCompanyName("");
            admin.setEmailNewsletter(false);
            admin.setVerify(true);
            userRepository.save(admin);
        }

        if (!adminPassword.isEmpty()) {
            /**
             * Makes sure the admin's password is overwritten with the one from overrides.properties
             * (in case it is set). This can be used as a last resort
             * if the admin has forgotten the password.
             */
            try {
                updateAndStoreUsersPassword(adminEmail, adminPassword);
            } catch (MessageIdException e) {
                throw new RuntimeException(messageSource.getMessage(e.getMessageId(), null, e.getMessageId(),
                        Locale.forLanguageTag("en")
                ));
            }
        }
    }

    private void updateAndStoreUsersPassword(String username, String newPassword) throws MessageIdException {
        logger.info("Updating '{}' password", username);
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            logger.warn("Cannot find user '{}'", username);
            return;
        }
        updateUserPassword(user.get(), newPassword);
        save(user.get());
    }

    public UserEntity updateUserPassword(UserEntity user, String newPassword) throws MessageIdException {
        if (ensureStrongPassword) {
            try {
                PasswordEncryption.ensureStrongPassword(newPassword);
            } catch (PasswordEncryption.WeakPasswordException e) {
                throw new MessageIdException(e.getMessage());
            }
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    public UserEntity createUser(UserDetailsDto userDetails) throws MessageIdException {
        while (true) {
            String uid = RandomNumberGenerator.generateUid(schoolUrl, "u");
            // check that uid is free!
            if (userRepository.findByUid(uid).isEmpty()) {
                return initializeNewUserDetails(new UserEntity(uid), userDetails);
            }
        }
    }

    private UserEntity initializeNewUserDetails(UserEntity user, UserDetailsDto userDetails) throws MessageIdException {
        if (userDetails.getPassword().isEmpty()) {
            throw new MessageIdException("auth.registration.passwordRequired");
        }
        initializeUserPasswordAndKeys(user, userDetails.getPassword());
        return updateUserDetails(user, userDetails);
    }

    public UserEntity updateUserDetails(UserEntity user, UserDetailsDto userDetails) throws MessageIdException {
        if (!userDetails.getEmail().contains("@")) {
            throw new MessageIdException("auth.registration.emailNeeded");
        }
        if (Objects.equals(userDetails.getFirstname(), "")) {
            throw new MessageIdException("user.update.firstnameEmpty");
        }
        if (!Objects.equals(userDetails.getEmail(), user.getEmail()) &&
                userRepository.existsByUsername(userDetails.getEmail())) {
            throw new MessageIdException("auth.registration.emailTaken");
        }

        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getEmail());

        user.setFirstName(userDetails.getFirstname());
        user.setLastName(userDetails.getLastname());
        user.setCompanyName(userDetails.getCompany());

        user.setStreet(userDetails.getStreet());
        user.setPostCode(userDetails.getZipcode());
        user.setCity(userDetails.getCity());
        user.setCountry(userDetails.getCountry());
        return user;
    }

    /**
     * TODO: merge partially with above updateUserDetails() method
     *
     * @param userId
     * @param data
     * @return
     */
    public UserEntity updateUserDetails(Integer userId, AdminUserDetailsDto data) throws MessageIdException {
        UserEntity user = userRepository.getById(userId);
        user.setId(data.getId());
        user.setUid(data.getUid());
//        user.setUsername(data.getUserDetails().getUsername());
        user.setEmail(data.getUserDetails().getEmail());
        updateUserPassword(user, data.getUserDetails().getPassword());
        user.setTitle(data.getUserDetails().getTitle());
        user.setFirstName(data.getUserDetails().getFirstname());
        user.setLastName(data.getUserDetails().getLastname());
//        user.setBirthDate(data.getUserDetails().getBirthDate());
        user.setCompanyName(data.getUserDetails().getCompany());
        user.setStreet(data.getUserDetails().getStreet());
        user.setPostCode(data.getUserDetails().getZipcode());
        user.setCity(data.getUserDetails().getCity());
        user.setCountry(data.getUserDetails().getCountry());
        user.setVerifyCode(data.getVerifyCode());
        user.setVerify(data.getIsVerify());
        user.setEmailNewsletter(data.getIsEmailNewsletter());
        user.setEmailRecovery(data.getEmailRecovery());
//        user.setLastEmailChange(data.getLastEmailChange());
        user.setPasswordReset(data.getPasswordReset());
        user.setComment(data.getComment());
        updateRoles(user, data);
        userRepository.save(user);
        return user;
    }

    private void updateRoles(UserEntity user, AdminUserDetailsDto data) {
        List<Role> toRemove = new ArrayList<>(user.getRoles());
        for (String role : data.getRoles()) {
            RoleType type = RoleType.valueOf(role);
            if (type == null) {
                // send MessageException
            }
            boolean isFound = false;
            for (Role toCheck : user.getRoles()) {
                if (toCheck.getName().equals(type)) {
                    toRemove.remove(role);
                    isFound = true;
                }
            }
            if (!isFound) {  // that is a new Role for the User
                Role newRole = new Role();
                newRole.setName(type);
                user.getRoles().add(newRole);
            }
        }
        for (Role role : toRemove) {
            user.getRoles().remove(role);
        }
    }

    public UserDetailsDto getUserDetails(UserEntity user) {
        UserDetailsDto userDetailsData = new UserDetailsDto();
        userDetailsData.setEmail(user.getEmail());
        userDetailsData.setTitle(user.getTitle());
        userDetailsData.setFirstname(user.getFirstName());
        userDetailsData.setLastname(user.getLastName());
        userDetailsData.setCompany(user.getCompanyName());
        userDetailsData.setStreet(user.getStreet());
        userDetailsData.setZipcode(user.getPostCode());
        userDetailsData.setCity(user.getCity());
        userDetailsData.setCountry(user.getCountry());
        userDetailsData.setBirthDate(user.getLocalBirthDate());
        return userDetailsData;
    }

    private void initializeUserPasswordAndKeys(UserEntity user, String password) throws MessageIdException {
        if (ensureStrongPassword) {
            try {
                PasswordEncryption.ensureStrongPassword(password);
            } catch (PasswordEncryption.WeakPasswordException e) {
                throw new MessageIdException(e.getMessage());
            }
        }
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        user.getRoles().add(roleRepository.findByName(RoleType.USER).orElseThrow());
    }


    public boolean sendVerificationMail(UserEntity user, String currentURL) {
        String verifyCode = RandomNumberGenerator.generateRandomString(32);
        user.setVerifyCode(verifyCode);
        boolean emailSend = false;
        try {
            emailSend = messagingService.sendVerificationMail(user, verifyCode, currentURL);
            if (!emailSend) {
                logger.warn("Verification mail could not be sent for user " + user.getEmail());
                user.setVerifyCode(null);
                user.setComment("exception while sending verification email. Try later on login, again. Please check " +
                        "Errorfile at: " + DateFunctions.getReadableTimestamp());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        save(user);
        return emailSend;
    }


    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }


    public Page<UserEntity> getUsersList(int page, int countPerPage) {
        Pageable pageable = PageRequest.of(page, countPerPage);
        return userRepository.findAll(pageable);
    }

    public boolean sendForgotPasswordMail(UserEntity user, String currentURL) {
        if (user.getLastPasswordReset() != null &&
                user.getLastPasswordReset().getTime() + 1000 * 60 * 60 * 24 > Instant.now().toEpochMilli()) {
            return false;
        }
        String resetCode = RandomNumberGenerator.generateRandomString(32);
        user.setPasswordReset(resetCode);
        user.setLastPasswordReset(new Date());
        boolean email = false;
        try {
            email = messagingService.sendForgotPasswordMail(user, resetCode, currentURL);
            if (!email) {
                user.setPasswordReset(null);
                user.setComment("exception while sending forgot password email. Try later on again. Please check " +
                        "Errorfile at: " + DateFunctions.getReadableTimestamp());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        save(user);
        return email;
    }

    public void verifyUser(String v, String email) throws MessageIdException {
        logger.info("Verification endpoint called with v=" + v + " and email=" + email);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (v.equals(user.get().getVerifyCode())) {
                user.get().setVerifyCode(null);
                user.get().setVerify(true);
                userRepository.save(user.get());
                return;
            }
            if (user.get().isVerify()) {
                logger.warn("User " + email + " already verified");
                throw new MessageIdException("auth.verify.alreadyVerified");
            }
            logger.warn("Wrong verification code " + v + " for " + email);
            throw new MessageIdException("auth.verify.wrongCode");
        }
        logger.warn("User " + email + " not found, Manipulationsversuch?");
        throw new MessageIdException("auth.verify.wrongEmail");
    }

    public void resetPassword(String code, String email, String newPassword,
                              String newPasswordRepeat) throws MessageIdException {
        if (!newPassword.equals(newPasswordRepeat)) {
            throw new MessageIdException("auth.resetPassword.passwordsNotEqual");
        }
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new MessageIdException("auth.resetPassword.userNotFound");
        }

        if (user.get().getLastPasswordReset() == null ||
                user.get().getLastPasswordReset().getTime() + 1000 * 60 * 60 * 24 < Instant.now().toEpochMilli()) {
            throw new MessageIdException("auth.resetPassword.timeExpired");
        }

        if (code.equals(user.get().getPasswordReset())) {
            updateUserPassword(user.get(), newPassword);
            user.get().setPasswordReset(null);
            user.get().setLastPasswordReset(new Date());
            userRepository.save(user.get());
        } else {
            throw new MessageIdException("auth.resetPassword.wrongCode");
        }
    }


    public void lockUserForSecounds(UserEntity user, int secounds) {
        user.setLockedUntil(DateFunctions.instantToDate(Instant.now().plusSeconds(30)));
        userRepository.save(user);
    }

    public String generateTelegramLink(UserEntity user) throws MessageIdException {
        if (user.getTelegramChatId() != 0) {
            throw new MessageIdException("telegrambot.link.alreadyLinked");
        }
        String digits = RandomNumberGenerator.generateRandomNumberString();
        user.setTelegramCode(digits);
        userRepository.save(user);
        return digits;
    }

    public void linkTelegramToAccount(long chatId, String linkUpCode) throws MessageIdException {
        Optional<UserEntity> user = userRepository.findByTelegramCode(linkUpCode);
        if (user.isEmpty()) {
            logger.warn("Telegram linkUpCode " + linkUpCode + " not found");
            throw new MessageIdException("telegrambot.link.fail");
        }
        user.get().setTelegramChatId(chatId);
        user.get().setTelegramCode(null);
        userRepository.save(user.get());
    }

    public String generateTelegramCode(UserEntity user) {
        String code = RandomNumberGenerator.generateRandomNumberString(5);
        user.setTelegramCode(code);
        userRepository.save(user);
        return code;
    }

    public void send2FACode(UserEntity user) {
        String code = generateTelegramCode(user);
        botApi.sendMessage(user.getTelegramChatId(), message("auth.login.twoFactorAuthCodeSend", new Object[]{code}));
    }

    String message(String identifier) {
        // Retrieve message string. Use the identifier as fallback if it cannot be found.
        // (this behavior is used, e.g., for the maintenance mode message)
        return messageSource.getMessage(identifier, null, identifier, LocaleContextHolder.getLocale());
    }

    String message(String identifier, Object[] vars) {
        return messageSource.getMessage(identifier, vars, LocaleContextHolder.getLocale());
    }
}
