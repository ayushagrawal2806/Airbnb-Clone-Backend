package com.airbnb.AirbnbClone.service.implementation;

import com.airbnb.AirbnbClone.entity.Booking;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.repository.BookingRepository;
import com.airbnb.AirbnbClone.service.CheckoutService;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;



    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        try {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(Objects.requireNonNull(user).getName())
                    .setEmail(user.getEmail())
                    .build();
            Customer customer = Customer.create(customerCreateParams);
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " : " + booking.getRoom().getId())
                                                                    .setDescription("Booking ID : " + booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
