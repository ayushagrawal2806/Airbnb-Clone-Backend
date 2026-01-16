package com.airbnb.AirbnbClone.repository;

import com.airbnb.AirbnbClone.entity.Guest;
import com.airbnb.AirbnbClone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}