package com.airbnb.AirbnbClone.entity;


import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer roomsCount;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false , precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(unique = true)
    private String paymentSessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(nullable = false , name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(nullable = false , name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(nullable = false , name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "booking_guest",
            inverseJoinColumns = @JoinColumn(
                    name = "guest_id"
            ),
            joinColumns = @JoinColumn(
                    name = "booking_id"
            )
    )
    private Set<Guest> guests;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
