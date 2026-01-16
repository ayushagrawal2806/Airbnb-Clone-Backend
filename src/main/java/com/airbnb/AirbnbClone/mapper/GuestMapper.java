package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.entity.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    GuestDto toDto(Guest guest);

    Guest toEntity(GuestDto guestDto);

    void updateGuestFromDto(GuestDto guestDto, @MappingTarget Guest guest);
}
