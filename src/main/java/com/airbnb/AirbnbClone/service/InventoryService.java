package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.dto.HotelDto;
import com.airbnb.AirbnbClone.dto.HotelPriceDto;
import com.airbnb.AirbnbClone.dto.HotelSearchRequest;
import com.airbnb.AirbnbClone.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventory(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
