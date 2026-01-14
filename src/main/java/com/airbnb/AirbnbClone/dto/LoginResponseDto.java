package com.airbnb.AirbnbClone.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
        "id",
        "name",
        "email",
        "accessToken",
        "refreshToken"
})
public class LoginResponseDto {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String name;
    private String email;

}
