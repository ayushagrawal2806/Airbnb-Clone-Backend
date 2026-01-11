package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room , Long> {
}
