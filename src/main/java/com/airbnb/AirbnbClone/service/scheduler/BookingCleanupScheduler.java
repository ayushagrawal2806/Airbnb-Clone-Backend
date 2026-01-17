package com.airbnb.AirbnbClone.service.scheduler;

import com.airbnb.AirbnbClone.entity.Booking;
import com.airbnb.AirbnbClone.entity.Inventory;
import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import com.airbnb.AirbnbClone.repository.BookingRepository;
import com.airbnb.AirbnbClone.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupScheduler {
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredBookings() {

        List<Booking> expiredBookings =
                bookingRepository.findByExpiresAtBeforeAndStatusNot(
                        LocalDateTime.now(),
                        BookingStatus.CONFIRMED
                );

        log.info("expireBookings {}" , expiredBookings.size());

        for (Booking booking : expiredBookings) {

            inventoryRepository.releaseReservedInventory(booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount());

            booking.setStatus(BookingStatus.EXPIRED);
            booking.setExpiresAt(null);
        }

        log.info("Expired bookings cleaned: {}", expiredBookings.size());
    }
}
