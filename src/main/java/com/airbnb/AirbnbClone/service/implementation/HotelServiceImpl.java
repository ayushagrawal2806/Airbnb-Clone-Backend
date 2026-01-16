package com.airbnb.AirbnbClone.service.implementation;


import com.airbnb.AirbnbClone.dto.*;
import com.airbnb.AirbnbClone.entity.Booking;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.exceptions.UnAuthorizedException;
import com.airbnb.AirbnbClone.mapper.HotelMapper;
import com.airbnb.AirbnbClone.mapper.RoomMapper;
import com.airbnb.AirbnbClone.repository.BookingRepository;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.RoomRepository;
import com.airbnb.AirbnbClone.service.HotelService;
import com.airbnb.AirbnbClone.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final BookingRepository bookingRepository;
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

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("hotel with this id is not found " + hotelId) );

        Hotel mappedHotel = hotelMapper.toEntity(hotelDto);
        mappedHotel.setId(hotelId);
        mappedHotel.setOwner(hotel.getOwner());
        log.info("hotel {}" , hotel.getOwner());
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
        mappedHotel = hotelRepository.save(mappedHotel);
        return hotelMapper.toDto(mappedHotel);

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
        if(hotel.getRoom().isEmpty()){
          throw new ResourceNotFoundException("Room not found with hotel id " + hotelId);
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

    @Override
    public List<HotelDto> getAllHotels() {
       User user = getCurrentUser();
       List<Hotel> hotels =  hotelRepository.findByOwnerOrderById(user);
       return hotels.stream().map(hotelMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id is not found" + hotelId));
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

       List<Booking> bookings =  bookingRepository.findByHotelAndCreatedAtBetween(hotel , startDateTime , endDateTime);
       Long totalConfirmedBooking =
               bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED).count();

       BigDecimal totalRevenue =
               bookings.stream()
                       .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                       .map(Booking::getAmount)
                       .reduce(BigDecimal.ZERO , BigDecimal::add);
       BigDecimal avgRevenue = totalRevenue.divide(BigDecimal.valueOf(totalConfirmedBooking) , RoundingMode.HALF_UP);



        return new HotelReportDto(totalConfirmedBooking , totalRevenue , avgRevenue);
    }


}
