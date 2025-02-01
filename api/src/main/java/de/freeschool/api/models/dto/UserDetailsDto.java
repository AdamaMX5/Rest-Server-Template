package de.freeschool.api.models.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * UserDetailsData
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-04T19:30:41" +
        ".560681200+02:00[Europe/Berlin]")
public class UserDetailsDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("title")
    private String title;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("birth_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @JsonProperty("company")
    private String company;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("country")
    private String country;

    public UserDetailsDto email(String email) {
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

    public UserDetailsDto password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Get password
     *
     * @return password
     */

    @Schema(name = "password", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDetailsDto title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */

    @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserDetailsDto firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    /**
     * Get firstname
     *
     * @return firstname
     */

    @Schema(name = "firstname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public UserDetailsDto lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    /**
     * Get lastname
     *
     * @return lastname
     */

    @Schema(name = "lastname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public UserDetailsDto birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    /**
     * Get birthDate
     *
     * @return birthDate
     */
    @Valid
    @Schema(name = "birth_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserDetailsDto company(String company) {
        this.company = company;
        return this;
    }

    /**
     * Get company
     *
     * @return company
     */

    @Schema(name = "company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public UserDetailsDto street(String street) {
        this.street = street;
        return this;
    }

    /**
     * Get street
     *
     * @return street
     */

    @Schema(name = "street", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public UserDetailsDto city(String city) {
        this.city = city;
        return this;
    }

    /**
     * Get city
     *
     * @return city
     */

    @Schema(name = "city", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public UserDetailsDto zipcode(String zipcode) {
        this.zipcode = zipcode;
        return this;
    }

    /**
     * Get zipcode
     *
     * @return zipcode
     */

    @Schema(name = "zipcode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public UserDetailsDto country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Get country
     *
     * @return country
     */

    @Schema(name = "country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsDto userDetailsData = (UserDetailsDto) o;
        return Objects.equals(this.email, userDetailsData.email) &&
                Objects.equals(this.password, userDetailsData.password) &&
                Objects.equals(this.title, userDetailsData.title) &&
                Objects.equals(this.firstname, userDetailsData.firstname) &&
                Objects.equals(this.lastname, userDetailsData.lastname) &&
                Objects.equals(this.birthDate, userDetailsData.birthDate) &&
                Objects.equals(this.company, userDetailsData.company) &&
                Objects.equals(this.street, userDetailsData.street) &&
                Objects.equals(this.city, userDetailsData.city) &&
                Objects.equals(this.zipcode, userDetailsData.zipcode) &&
                Objects.equals(this.country, userDetailsData.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, title, firstname, lastname, birthDate, company, street, city, zipcode,
                country
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserDetailsData {\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    firstname: ").append(toIndentedString(firstname)).append("\n");
        sb.append("    lastname: ").append(toIndentedString(lastname)).append("\n");
        sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
        sb.append("    company: ").append(toIndentedString(company)).append("\n");
        sb.append("    street: ").append(toIndentedString(street)).append("\n");
        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
        sb.append("    country: ").append(toIndentedString(country)).append("\n");
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

