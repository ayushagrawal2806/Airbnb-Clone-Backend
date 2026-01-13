package com.airbnb.AirbnbClone.strategy;

import com.airbnb.AirbnbClone.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService  {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy priceStrategy = new BasePriceStrategy();

        priceStrategy = new SurgePricingStrategy(priceStrategy);
        priceStrategy = new OccupancyPricingStrategy(priceStrategy);
        priceStrategy = new UrgencyPricingStrategy(priceStrategy);
        priceStrategy = new HolidayPricingStrategy(priceStrategy);


        return priceStrategy.calculatePrice(inventory);

    }
}
