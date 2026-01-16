package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;
import com.stripe.model.Event;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);

    BookingDto addGuest(Long bookingId, List<Long> guestIdList);


    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingByHotelId(Long hotelId);

    List<BookingDto> getMyBookings();
}
