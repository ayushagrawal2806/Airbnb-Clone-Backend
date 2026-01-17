package com.airbnb.AirbnbClone.service.scheduler;

import com.airbnb.AirbnbClone.entity.Hotel;
import com.airbnb.AirbnbClone.entity.HotelMinPrice;
import com.airbnb.AirbnbClone.entity.Inventory;
import com.airbnb.AirbnbClone.repository.HotelMinPriceRepository;
import com.airbnb.AirbnbClone.repository.HotelRepository;
import com.airbnb.AirbnbClone.repository.InventoryRepository;
import com.airbnb.AirbnbClone.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    // Scheduler to update the inventory and HotelMinPrice tables every year

    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * *") // this function will every in every one hour
    public void updatePrices(){
        int page = 0;
        int batchSize = 100;
        log.info("cron job is running properly");

        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page , batchSize));
            if(hotelPage.isEmpty()){
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrices);
            page++;
        }

    }

    public void updateHotelPrices(Hotel hotel){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel , startDate , endDate);

        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel , inventoryList , startDate , endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate , BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice , Collectors.minBy(Comparator.naturalOrder()))
                )).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry :: getKey , e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date , price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel , date)
                    .orElse(new HotelMinPrice(hotel , date));

            hotelMinPrice.setPrice(price);
            hotelPrices.add(hotelMinPrice);

        });

        hotelMinPriceRepository.saveAll(hotelPrices);

    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {

        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryList);
    }
}
