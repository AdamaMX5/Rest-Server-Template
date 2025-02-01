package de.freeschool.api.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdminUserDetailsData
 */
@Data
@NoArgsConstructor
public class UsersListDto {


    @JsonProperty("users")
    @Valid
    private List<AdminUserDetailsDto> users = new ArrayList<>();


}

