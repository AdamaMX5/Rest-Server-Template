package de.freeschool.api.models.response;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * LoginResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class LoginResponse {

    @JsonProperty("jwtToken")
    private String jwtToken;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("twoFactorAuthEnabled")
    private Boolean twoFactorAuthEnabled;

    /**
     * Gets or Sets roles
     */
    public enum RolesEnum {
        USER("USER"),

        ADMIN("ADMIN");

        private String value;

        RolesEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static RolesEnum fromValue(String value) {
            for (RolesEnum b : RolesEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @JsonProperty("roles")
    @Valid
    private List<RolesEnum> roles = null;

    public LoginResponse jwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        return this;
    }

    /**
     * JWT-Session token, you need to send it in Header with 'Bearer jwtToken'
     *
     * @return jwtToken
     */

    @Schema(name = "jwtToken", description = "JWT-Session token, you need to send it in Header with 'Bearer " +
            "jwtToken'", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LoginResponse firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * User's first name
     *
     * @return firstName
     */

    @Schema(name = "firstName", description = "User's first name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LoginResponse lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * User's last name
     *
     * @return lastName
     */

    @Schema(name = "lastName", description = "User's last name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LoginResponse twoFactorAuthEnabled(Boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
        return this;
    }

    /**
     * True if 2FA is enabled, false if not. If true, the user has to enter the 2FA code after login
     *
     * @return twoFactorAuthEnabled
     */

    @Schema(name = "twoFactorAuthEnabled", description = "True if 2FA is enabled, false if not. If true, the user has" +
            " to enter the 2FA code after login", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public Boolean getTwoFactorAuthEnabled() {
        return twoFactorAuthEnabled;
    }

    public void setTwoFactorAuthEnabled(Boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
    }

    public LoginResponse roles(List<RolesEnum> roles) {
        this.roles = roles;
        return this;
    }

    public LoginResponse addRolesItem(RolesEnum rolesItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(rolesItem);
        return this;
    }

    /**
     * User's roles
     *
     * @return roles
     */

    @Schema(name = "roles", description = "User's roles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public List<RolesEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RolesEnum> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginResponse loginResponse = (LoginResponse) o;
        return Objects.equals(this.jwtToken, loginResponse.jwtToken) &&
                Objects.equals(this.firstName, loginResponse.firstName) &&
                Objects.equals(this.lastName, loginResponse.lastName) &&
                Objects.equals(this.twoFactorAuthEnabled, loginResponse.twoFactorAuthEnabled) &&
                Objects.equals(this.roles, loginResponse.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwtToken, firstName, lastName, twoFactorAuthEnabled, roles);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LoginResponse {\n");
        sb.append("    jwtToken: ").append(toIndentedString(jwtToken)).append("\n");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
        sb.append("    twoFactorAuthEnabled: ").append(toIndentedString(twoFactorAuthEnabled)).append("\n");
        sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
        sb.append("}");
        return sb.toString();
    }

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

