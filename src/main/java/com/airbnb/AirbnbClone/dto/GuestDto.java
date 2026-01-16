package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "gender",
        "dateOfBirth"
})
public class GuestDto {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
