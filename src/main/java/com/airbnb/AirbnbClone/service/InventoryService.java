package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.HotelPriceDto;
import com.airbnb.AirbnbClone.dto.HotelSearchRequest;
import com.airbnb.AirbnbClone.dto.InventoryDto;
import com.airbnb.AirbnbClone.dto.UpdateInventoryRequestDto;
import com.airbnb.AirbnbClone.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventory(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoomAndHotel(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
