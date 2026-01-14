package com.airbnb.AirbnbClone.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "email"
})
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
