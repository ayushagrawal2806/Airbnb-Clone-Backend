package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.RoomDto;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.mapper.RoomMapper;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a room inside hotel with id{}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));

        Room room = roomMapper.toEntity(roomDto);

        room.setHotel(hotel);
        roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return roomMapper.toDto(room);

    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms inside hotel with id{}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));

        return hotel.getRoom().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id not found" + roomId));
        return roomMapper.toDto(room);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id not found" + roomId));

//       this line will delete all inventory of that particular room
        inventoryService.deleteAllInventory(room);
        roomRepository.deleteById(roomId);

    }
}
