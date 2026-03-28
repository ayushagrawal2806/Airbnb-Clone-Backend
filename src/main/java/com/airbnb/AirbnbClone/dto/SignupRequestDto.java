package com.airbnb.AirbnbClone.dto;


import com.airbnb.AirbnbClone.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String email;
    private String password;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;

}
