package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelPriceDto;
import com.airbnb.AirbnbClone.entity.Hotel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelDto toDto(Hotel hotel) ;

    Hotel toEntity(HotelDto hotelDto);

    HotelPriceDto toHotelPriceDto(Hotel hotel);


}
