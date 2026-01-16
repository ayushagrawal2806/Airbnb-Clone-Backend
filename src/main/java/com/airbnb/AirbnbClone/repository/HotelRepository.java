package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel , Long> {
    List<Hotel> findByOwnerOrderById(User user);
}
