package de.freeschool.api.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;

/**
 * AdminUserDetailsData
 */
@Data
@NoArgsConstructor
public class AdminUserDetailsDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("user_details")
    private UserDetailsDto userDetails;

    @JsonProperty("verify_code")
    private String verifyCode;

    @JsonProperty("is_verify")
    private Boolean isVerify;

    @JsonProperty("is_email_newsletter")
    private Boolean isEmailNewsletter;


    @JsonProperty("email_recovery")
    private String emailRecovery;

    @JsonProperty("last_email_change")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastEmailChange;

    @JsonProperty("password_reset")
    private String passwordReset;

    @JsonProperty("last_password_reset")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastPasswordReset;


    @JsonProperty("comment")
    private String comment;

    @JsonProperty("roles")
    @Valid
    private List<String> roles = null;


    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

