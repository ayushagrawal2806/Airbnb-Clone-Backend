package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.entity.Guest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    GuestDto toDto(Guest guest);

    Guest toEntity(GuestDto guestDto);
}
