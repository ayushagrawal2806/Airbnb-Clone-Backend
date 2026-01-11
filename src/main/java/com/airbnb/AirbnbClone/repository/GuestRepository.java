package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}