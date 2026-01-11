package com.airbnb.AirbnbClone.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
