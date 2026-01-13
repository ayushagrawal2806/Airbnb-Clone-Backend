package com.airbnb.AirbnbClone.strategy;

import com.airbnb.AirbnbClone.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return pricingStrategy.calculatePrice(inventory).multiply(inventory.getSurgeFactor());
    }
}
