package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toDto(Booking booking);
}
