package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "email",
        "gender",
        "dateOfBirth"
})
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
