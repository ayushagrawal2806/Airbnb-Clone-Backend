package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.controller.HotelController;
import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelInfoDto;
import com.airbnb.AirbnbClone.dto.HotelReportDto;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    HotelDto createHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotel(Long hotelId, HotelDto hotelDto);

    void deleteHotel(Long hotelId);

    void activateHotel(Long hotelId);

   HotelInfoDto getHotelInfo(Long hotelId);

   List<HotelDto> getAllHotels();


   HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
}
