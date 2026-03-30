package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import com.airbnb.AirbnbClone.entity.enums.UserRoles;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProfileUpdateRequestDto {

    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private UserRoles role;
}
