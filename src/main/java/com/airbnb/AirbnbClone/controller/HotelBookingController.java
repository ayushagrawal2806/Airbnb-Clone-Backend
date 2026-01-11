package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequestDto){
        BookingDto bookingDto = bookingService.initialiseBooking(bookingRequestDto);
        return ResponseEntity.ok(bookingDto);
    }

    @PostMapping("/{bookingId}/addGuest")
    public ResponseEntity<BookingDto> addGuest(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList){
        BookingDto bookingDto = bookingService.addGuest(bookingId , guestDtoList);
        return ResponseEntity.ok(bookingDto);
    }
}
