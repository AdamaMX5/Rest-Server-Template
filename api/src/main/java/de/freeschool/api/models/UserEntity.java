package de.freeschool.api.models;

import de.freeschool.api.models.type.RoleType;
import de.freeschool.api.util.RandomNumberGenerator;
import de.freeschool.api.models.response.LoginResponse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    public static UserEntity create(String domain, String username, String email, String password,
                                    Role mainRole) throws InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException {
        UserEntity user = new UserEntity();
        user.setUid(RandomNumberGenerator.generateUid(domain, "u"));
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.getRoles().add(mainRole);
        return user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String uid;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private Date lockedUntil;

    private String title;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String companyName;
    private String street;
    private String postCode;
    private String city;
    private String country;

    private String telegramCode;
    private long telegramChatId;
    private String twoFactorCode;

    private String verifyCode;
    private boolean verify = false;
    private boolean emailNewsletter = true;
    private String emailRecovery;
    private Date lastEmailChange;
    private String passwordReset;
    private Date lastPasswordReset;
    private String comment;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    // fetch, sayes, that all roles are loaded with the userload from Database in 1 query.
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();


    public UserEntity(String uid) {
        this.uid = uid;
    }

    public String getEmailOrUsername() {
        if (StringUtils.hasText(username)) {
            return username;
        }
        return email;
    }

    public List<LoginResponse.RolesEnum> getRoleNames() {
        return getRoles().stream()
                .map(role -> LoginResponse.RolesEnum.fromValue(role.getNameString()))
                .collect(Collectors.toList());
    }

    public String getFullName() {
        StringBuilder name = new StringBuilder();
        if (firstName != null && lastName != null) {
            name.append(firstName).append(" ").append(lastName);
        } else if (firstName != null) {
            name.append(firstName);
        } else if (lastName != null) {
            name.append(lastName);
        } else {
            return null;
        }
        return name.toString();
    }

    public String toString() {
        String fullName = getFullName();
        if (fullName != null) {
            return fullName;
        }
        return uid;
    }

    public boolean isAdmin() {
        return getRoles().stream().anyMatch(r -> r.getName() == RoleType.ADMIN);
    }

    public boolean isUser() {
        return getRoles().stream().anyMatch(r -> r.getName() == RoleType.USER);
    }


    public LocalDate getLocalBirthDate() {
        if (getBirthDate() == null) {
            return null;
        }
        return getBirthDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }

    public boolean is2FA() {
        return getTelegramChatId() != 0;
    }
}
