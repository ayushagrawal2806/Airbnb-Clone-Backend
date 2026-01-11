package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "gender",
        "age",
        "user"
})
public class GuestDto {
    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
    private User user;
}
