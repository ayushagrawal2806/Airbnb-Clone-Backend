package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.Hotel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonPropertyOrder({
        "id",
        "type",
        "basePrice",
        "totalCount",
        "capacity",
        "photos",
        "amenities"
})
public class RoomDto {

    private Long id;
    private String type;
    private BigDecimal basePrice;
    private String[] photos;
    private String[] amenities;
    private Integer totalCount;
    private Integer capacity;
}
