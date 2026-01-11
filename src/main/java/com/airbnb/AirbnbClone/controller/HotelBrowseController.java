package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelInfoDto;
import com.airbnb.AirbnbClone.dto.HotelSearchRequest;
import com.airbnb.AirbnbClone.repository.InventoryRepository;
import com.airbnb.AirbnbClone.service.HotelService;
import com.airbnb.AirbnbClone.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    public final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDto> hotel = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfo(hotelId);
        return ResponseEntity.ok(hotelInfoDto);
    }

}
