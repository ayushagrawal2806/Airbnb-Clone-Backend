package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.InventoryDto;
import com.airbnb.AirbnbClone.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDto toDto(Inventory inventory);
}
