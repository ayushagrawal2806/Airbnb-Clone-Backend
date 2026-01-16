package com.airbnb.AirbnbClone.dto;


import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.Room;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "id",
        "date",
        "price",
        "surgeFactor",
        "bookedCount",
        "reservedCount",
        "totalCount",
        "closed",
        "createdAt",
        "updatedAt"

})
public class InventoryDto {

    private Long id;
    private Integer bookedCount;
    private Integer totalCount;
    private Integer reservedCount;
    private BigDecimal surgeFactor;
    private Boolean closed;
    private BigDecimal price;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
