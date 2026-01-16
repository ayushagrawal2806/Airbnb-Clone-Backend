package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelReportDto;
import com.airbnb.AirbnbClone.service.BookingService;
import com.airbnb.AirbnbClone.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

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

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId){
        return ResponseEntity.ok(bookingService.getAllBookingByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/hotelReport")
    public ResponseEntity<HotelReportDto> getRHotelReport(@PathVariable Long hotelId,
                                                          @RequestParam(required = false) LocalDate startDate,
                                                          @RequestParam(required = false) LocalDate endDate){
        if(startDate == null) startDate = LocalDate.now().minusMonths(1);
        if(endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(hotelService.getHotelReport(hotelId , startDate , endDate));
    }
}
