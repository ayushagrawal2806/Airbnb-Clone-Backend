package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.RoomDto;
import org.jspecify.annotations.Nullable;

import java.util.List;


public interface RoomService {

    RoomDto createRoom(Long hotelId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    RoomDto updateRoomById(Long roomId, Long hotelId, RoomDto roomDto);
}
