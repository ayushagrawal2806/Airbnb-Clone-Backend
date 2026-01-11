package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelSearchRequest;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Inventory;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.mapper.HotelMapper;
import com.airbnb.AirbnbClone.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final HotelMapper hotelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(;!today.isAfter(endDate); today = today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .date(today)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventory(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage() , hotelSearchRequest.getSize());
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate() , hotelSearchRequest.getEndDate()) + 1;
        Page<Hotel> hotelPage = inventoryRepository.findHotelWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable);

        return hotelPage.map(hotelMapper::toDto);
    }
}
