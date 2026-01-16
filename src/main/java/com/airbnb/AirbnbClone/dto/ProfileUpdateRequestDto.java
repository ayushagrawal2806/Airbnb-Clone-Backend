package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {

    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
