package com.airbnb.AirbnbClone.service.implementation;

import com.airbnb.AirbnbClone.dto.*;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Inventory;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.mapper.HotelMapper;
import com.airbnb.AirbnbClone.mapper.InventoryMapper;
import com.airbnb.AirbnbClone.repository.HotelMinPriceRepository;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.InventoryRepository;
import com.airbnb.AirbnbClone.repository.RoomRepository;
import com.airbnb.AirbnbClone.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final HotelRepository hotelRepository;
    private final InventoryMapper inventoryMapper;
    private final RoomRepository roomRepository;

    private final InventoryRepository inventoryRepository;
    private final HotelMapper hotelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

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
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info(hotelSearchRequest.getPage().toString());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage() , hotelSearchRequest.getSize());
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate() , hotelSearchRequest.getEndDate()) + 1;
        Page<HotelPriceEntityDto> hotelPage = hotelMinPriceRepository.findHotelWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable);

        log.info(hotelPage.toString());

//        return hotelPage.map(hotelMapper::toDto);
         return hotelPage.map(entity ->
                new HotelPriceDto(
                        hotelMapper.toDto(entity.getHotel()), // Hotel → HotelDto
                        entity.getPrice()
                )
        );
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoomAndHotel(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFoundException("Room not found with this id" + roomId)
        );

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of room with id" + roomId);
        }
        List<Inventory> inventories = inventoryRepository.findByRoomOrderByDate(room);
        return inventories.stream().map(inventoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto){

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id" + roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of room with id" + roomId);
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId , updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId , updateInventoryRequestDto.getStartDate() , updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(),updateInventoryRequestDto.getSurgeFactor());

    }
}
