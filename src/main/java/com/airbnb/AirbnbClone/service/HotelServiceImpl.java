package com.airbnb.AirbnbClone.service;


import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelInfoDto;
import com.airbnb.AirbnbClone.dto.RoomDto;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.exceptions.UnAuthorizedException;
import com.airbnb.AirbnbClone.mapper.HotelMapper;
import com.airbnb.AirbnbClone.mapper.RoomMapper;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{
    private final RoomMapper roomMapper;

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;


    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("creating a hotel with name {}" , hotelDto.getName());
        Hotel hotel = hotelMapper.toEntity(hotelDto);
        log.info("AFTER MAPPING → entity.name = {}", hotel.getName());
        hotel.setActive(false);
        User user = getCurrentUser();
        hotel.setOwner(user);
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with Id {}" , hotel.getId());
        return hotelMapper.toDto(hotel);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting hotel with id {}" , id);
        Hotel hotel =  hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("hotel not found with id" + id));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
        return hotelMapper.toDto(hotel);
    }

    @Override
    public HotelDto updateHotel(Long hotelId, HotelDto hotelDto) {

        boolean exists = hotelRepository.existsById(hotelId);
        if(!exists){
            throw new ResourceNotFoundException("hotel with this id is not found " + hotelId);
        }
        Hotel hotel = hotelMapper.toEntity(hotelDto);
        hotel.setId(hotelId);

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toDto(hotel);

    }

    @Override
    @Transactional
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id is not found" + hotelId));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }

        for(Room room : hotel.getRoom()){
            inventoryService.deleteAllInventory(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(hotelId);


    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
       Hotel hotel = hotelRepository
               .findById(hotelId)
               .orElseThrow(() -> new ResourceNotFoundException("Hotel with id is not found" + hotelId));
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
       hotel.setActive(true);
       for(Room room : hotel.getRoom()){
           inventoryService.initializeRoomForAYear(room);
       }
       hotelRepository.save(hotel);

    }

    @Override
    public HotelInfoDto getHotelInfo(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id is not found" + hotelId));
        List<RoomDto> rooms = hotel.getRoom().stream().map(roomMapper::toDto).toList();
        HotelDto hotelDto = hotelMapper.toDto(hotel);
        return HotelInfoDto.builder()
                .hotel(hotelDto)
                .rooms(rooms)
                .build();
    }

    public User getCurrentUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
