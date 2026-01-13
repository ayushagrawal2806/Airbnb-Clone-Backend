package com.airbnb.AirbnbClone.strategy;

import com.airbnb.AirbnbClone.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
       BigDecimal price = pricingStrategy.calculatePrice(inventory);
       double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
       if(occupancyRate > 0.8){
           price = price.multiply(BigDecimal.valueOf(1.2));
       }
       return price;
    }
}
