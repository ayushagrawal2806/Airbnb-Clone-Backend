package com.airbnb.AirbnbClone.strategy;

import com.airbnb.AirbnbClone.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
