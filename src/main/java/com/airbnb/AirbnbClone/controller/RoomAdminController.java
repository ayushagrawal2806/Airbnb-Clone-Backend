package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.dto.RoomDto;
import com.airbnb.AirbnbClone.service.RoomService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId , @RequestBody RoomDto roomDto){
        RoomDto room = roomService.createRoom(hotelId , roomDto);
       return new ResponseEntity<>( room,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId){
        List<RoomDto> rooms = roomService.getAllRoomsInHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId){
        RoomDto rooms = roomService.getRoomById(roomId);
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long roomId,
                                                  @PathVariable Long hotelId,
                                                  @RequestBody RoomDto roomDto){
    return ResponseEntity.ok(roomService.updateRoomById(roomId , hotelId , roomDto));
    };
}
