package com.airbnb.AirbnbClone.service.implementation;

import com.airbnb.AirbnbClone.dto.RoomDto;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.exceptions.UnAuthorizedException;
import com.airbnb.AirbnbClone.mapper.RoomMapper;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.RoomRepository;
import com.airbnb.AirbnbClone.service.InventoryService;
import com.airbnb.AirbnbClone.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

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
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }

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
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
        inventoryService.deleteAllInventory(room);
        roomRepository.deleteById(roomId);

    }

    @Override
    public RoomDto updateRoomById(Long roomId, Long hotelId, RoomDto roomDto) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id"  + roomId));


//        TODO if price or inventory is updated then update the inventory for this room also
        roomMapper.updateRoomFromDto(roomDto, room);
        room.setId(roomId);
        room = roomRepository.save(room);

        return roomMapper.toDto(room);
    }


}
