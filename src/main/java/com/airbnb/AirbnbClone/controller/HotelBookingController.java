package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String , String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl = bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok( Map.of("sessionUrl" , sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<Map<String , String>> getBookingStatus(@PathVariable Long bookingId){
        String bookingStatus = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok( Map.of("bookingStatus" , bookingStatus));
    }
}
