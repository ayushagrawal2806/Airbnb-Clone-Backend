package com.airbnb.AirbnbClone.service;

import com.airbnb.AirbnbClone.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking , String successUrl , String failureUrl);
}
