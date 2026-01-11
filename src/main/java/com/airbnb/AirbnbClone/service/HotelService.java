package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.controller.HotelController;
import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelInfoDto;
import org.springframework.http.ResponseEntity;

public interface HotelService {

    HotelDto createHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotel(Long hotelId, HotelDto hotelDto);

    void deleteHotel(Long hotelId);

    void activateHotel(Long hotelId);

   HotelInfoDto getHotelInfo(Long hotelId);
}
