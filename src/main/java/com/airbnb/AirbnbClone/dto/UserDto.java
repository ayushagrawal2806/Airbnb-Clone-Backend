package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import com.airbnb.AirbnbClone.entity.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "email",
        "gender",
        "dateOfBirth",
        "role"
})
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private UserRoles role;
}
