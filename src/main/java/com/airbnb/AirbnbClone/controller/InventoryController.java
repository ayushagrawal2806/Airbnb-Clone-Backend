package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.InventoryDto;
import com.airbnb.AirbnbClone.dto.UpdateInventoryRequestDto;
import com.airbnb.AirbnbClone.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoomAndHotel(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventoryByRoom(@PathVariable Long roomId,
                                                      @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto){
        inventoryService.updateInventory(roomId , updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }

}
