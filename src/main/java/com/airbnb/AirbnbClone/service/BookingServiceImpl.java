package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.entity.*;
import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.mapper.BookingMapper;
import com.airbnb.AirbnbClone.mapper.GuestMapper;
import com.airbnb.AirbnbClone.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto bookingRequestDto) {
        Hotel hotel = hotelRepository
                .findById(bookingRequestDto.getHotelId()).orElseThrow(() ->
                        new ResourceNotFoundException("hotel not found with this id" + bookingRequestDto.getHotelId()));

        Room room = roomRepository
                .findById(bookingRequestDto.getRoomId()).orElseThrow(() ->
                        new ResourceNotFoundException("Room not found with this id" + bookingRequestDto.getRoomId()));

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getRoomsCount()

        );

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate() , bookingRequestDto.getCheckOutDate()) + 1;
        if(inventories.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        for(Inventory inventory : inventories){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomsCount());
        }

        inventoryRepository.saveAll(inventories);


//        TODO: Remove dummy user;


//        TODO: Calculate dynamic price;

        Booking booking = Booking.builder()
                .roomsCount(bookingRequestDto.getRoomsCount())
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .status(BookingStatus.RESERVED)
                .user(getCurrentUser())
                .amount(BigDecimal.TEN)
                .build();

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);

    }

    @Override
    @Transactional
    public BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is already expired");
        }

        if(booking.getStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = guestMapper.toEntity(guestDto);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuest().add(guest);
        }

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }


    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user = new User();
        user.setId(1L);
        user.setName("ayush");
        user.setEmail("ayush@gmail.com");

        return user;
    }
}
