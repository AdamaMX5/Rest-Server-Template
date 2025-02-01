package de.freeschool.api.models.request;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * ForgotPasswordRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class ForgotPasswordRequest {

    @JsonProperty("email")
    private String email;

    public ForgotPasswordRequest email(String email) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ForgotPasswordRequest forgotPasswordRequest = (ForgotPasswordRequest) o;
        return Objects.equals(this.email, forgotPasswordRequest.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ForgotPasswordRequest {\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

