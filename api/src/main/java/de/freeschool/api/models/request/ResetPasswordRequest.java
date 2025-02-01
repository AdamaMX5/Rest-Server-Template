package de.freeschool.api.models.request;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * ResetPasswordRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class ResetPasswordRequest {

    @JsonProperty("code")
    private String code;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("passwordRepeat")
    private String passwordRepeat;

    public ResetPasswordRequest code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     */
    @NotNull
    @Schema(name = "code", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResetPasswordRequest email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     *
     * @return email
     */
    @NotNull
    @Schema(name = "email", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ResetPasswordRequest password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Get password
     *
     * @return password
     */
    @NotNull
    @Schema(name = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ResetPasswordRequest passwordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
        return this;
    }

    /**
     * Get passwordRepeat
     *
     * @return passwordRepeat
     */
    @NotNull
    @Schema(name = "passwordRepeat", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResetPasswordRequest resetPasswordRequest = (ResetPasswordRequest) o;
        return Objects.equals(this.code, resetPasswordRequest.code) &&
                Objects.equals(this.email, resetPasswordRequest.email) &&
                Objects.equals(this.password, resetPasswordRequest.password) &&
                Objects.equals(this.passwordRepeat, resetPasswordRequest.passwordRepeat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, email, password, passwordRepeat);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResetPasswordRequest {\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    passwordRepeat: ").append(toIndentedString(passwordRepeat)).append("\n");
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

