package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {


    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "guests" , source = "guests")
    BookingDto toDto(Booking booking);
}
