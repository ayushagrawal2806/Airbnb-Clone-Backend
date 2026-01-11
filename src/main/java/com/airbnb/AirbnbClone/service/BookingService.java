package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);

    BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList);
}
