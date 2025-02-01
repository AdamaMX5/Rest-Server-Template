package de.freeschool.api.models.request;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * TwoFactorAuthRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class TwoFactorAuthRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("twoFactorAuthCode")
    private String twoFactorAuthCode;

    public TwoFactorAuthRequest email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     *
     * @return email
     */

    @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TwoFactorAuthRequest twoFactorAuthCode(String twoFactorAuthCode) {
        this.twoFactorAuthCode = twoFactorAuthCode;
        return this;
    }

    /**
     * Get twoFactorAuthCode
     *
     * @return twoFactorAuthCode
     */
    @NotNull
    @Schema(name = "twoFactorAuthCode", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getTwoFactorAuthCode() {
        return twoFactorAuthCode;
    }

    public void setTwoFactorAuthCode(String twoFactorAuthCode) {
        this.twoFactorAuthCode = twoFactorAuthCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TwoFactorAuthRequest twoFactorAuthRequest = (TwoFactorAuthRequest) o;
        return Objects.equals(this.email, twoFactorAuthRequest.email) &&
                Objects.equals(this.twoFactorAuthCode, twoFactorAuthRequest.twoFactorAuthCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, twoFactorAuthCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TwoFactorAuthRequest {\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    twoFactorAuthCode: ").append(toIndentedString(twoFactorAuthCode)).append("\n");
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

