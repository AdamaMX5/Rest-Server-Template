package de.freeschool.api.models.response;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * RegisterResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class RegisterResponse {

    @JsonProperty("message")
    private String message;

    public RegisterResponse message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     *
     * @return message
     */

    @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterResponse registerResponse = (RegisterResponse) o;
        return Objects.equals(this.message, registerResponse.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RegisterResponse {\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

