package com.airbnb.AirbnbClone.dto;

import com.airbnb.AirbnbClone.entity.Guest;
import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonPropertyOrder({
        "id",
        "roomsCount",
        "status",
        "guest",
        "amount",
        "checkInDate",
        "checkOutDate",
        "createdAt",
        "updatedAt",

})
public class BookingDto {
    private Long id;
    private Integer roomsCount;

    private LocalDate checkInDate;


    private LocalDate checkOutDate;

    private BookingStatus status;

    private Set<GuestDto> guest;

    private BigDecimal amount;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


}
