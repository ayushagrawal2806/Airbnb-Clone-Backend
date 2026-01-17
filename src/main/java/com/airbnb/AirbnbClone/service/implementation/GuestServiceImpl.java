package com.airbnb.AirbnbClone.service.implementation;


import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.entity.Guest;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.mapper.GuestMapper;
import com.airbnb.AirbnbClone.repository.GuestRepository;
import com.airbnb.AirbnbClone.service.GuestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {
    private final GuestMapper guestMapper;

    private final GuestRepository guestRepository;


    @Override
    public List<GuestDto> getAllGuests() {
        User user = getCurrentUser();
        log.info("Fetching all guests of user with id: {}", user.getId());
        List<Guest> guests = guestRepository.findByUserAndActiveTrue(user);
        return guests.stream().map(guestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto) {
        log.info("Adding new guest: {}", guestDto);
        User user = getCurrentUser();
        Guest guest = guestMapper.toEntity(guestDto);
        guest.setUser(user);
        Guest savedGuest = guestRepository.save(guest);
        log.info("Guest added with ID: {}", savedGuest.getId());
        return guestMapper.toDto(savedGuest);
    }

    @Override
    public void updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");


        guestMapper.updateGuestFromDto(guestDto , guest);
        guest.setUser(user);
        guest.setId(guestId);

        guestRepository.save(guest);
        log.info("Guest with ID: {} updated successfully", guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guest.setActive(false);
        guestRepository.save(guest);
        log.info("Guest with ID: {} deleted successfully", guestId);
    }
}
