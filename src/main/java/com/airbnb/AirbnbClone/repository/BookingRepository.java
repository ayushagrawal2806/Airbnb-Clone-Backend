package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking , Long> {

}
