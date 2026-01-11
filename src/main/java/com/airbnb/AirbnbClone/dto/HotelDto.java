package com.airbnb.AirbnbClone.dto;


import com.airbnb.AirbnbClone.entity.HotelContactInfo;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "city",
        "photos",
        "amenities",
        "contactInfo",
        "active"
})
public class HotelDto {

    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private Boolean active;
    private HotelContactInfo contactInfo;

}
