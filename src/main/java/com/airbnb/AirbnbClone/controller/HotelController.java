package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto){
        HotelDto hotel = hotelService.createHotel(hotelDto);
        return new ResponseEntity<>(hotel , HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long hotelId , @RequestBody HotelDto hotelDto){
        HotelDto hotel = hotelService.updateHotel( hotelId , hotelDto);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId ){
        hotelService.deleteHotel( hotelId );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId ){
        hotelService.activateHotel( hotelId );
        return ResponseEntity.noContent().build();
    }
}
