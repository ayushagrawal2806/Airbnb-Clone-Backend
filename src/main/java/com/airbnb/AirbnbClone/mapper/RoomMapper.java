package com.airbnb.AirbnbClone.mapper;


import com.airbnb.AirbnbClone.dto.RoomDto;
import com.airbnb.AirbnbClone.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDto toDto(Room room);

    Room toEntity(RoomDto roomDto);
}
