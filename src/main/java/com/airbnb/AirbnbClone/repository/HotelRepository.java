package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel , Long> {
}
