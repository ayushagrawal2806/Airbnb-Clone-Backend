package com.airbnb.AirbnbClone.service.implementation;

import com.airbnb.AirbnbClone.dto.BookingDto;
import com.airbnb.AirbnbClone.dto.BookingRequestDto;
import com.airbnb.AirbnbClone.dto.GuestDto;
import com.airbnb.AirbnbClone.entity.*;
import com.airbnb.AirbnbClone.entity.enums.BookingStatus;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.exceptions.UnAuthorizedException;
import com.airbnb.AirbnbClone.mapper.BookingMapper;
import com.airbnb.AirbnbClone.mapper.GuestMapper;
import com.airbnb.AirbnbClone.repository.*;
import com.airbnb.AirbnbClone.service.BookingService;
import com.airbnb.AirbnbClone.service.CheckoutService;
import com.airbnb.AirbnbClone.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontEnd-url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto bookingRequestDto) {
        Hotel hotel = hotelRepository
                .findById(bookingRequestDto.getHotelId()).orElseThrow(() ->
                        new ResourceNotFoundException("hotel not found with this id " + bookingRequestDto.getHotelId()));

        Room room = roomRepository
                .findById(bookingRequestDto.getRoomId()).orElseThrow(() ->
                        new ResourceNotFoundException("Room not found with this id " + bookingRequestDto.getRoomId()));

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getRoomsCount()

        );

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate() , bookingRequestDto.getCheckOutDate()) + 1;
        if(inventories.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        inventoryRepository.initBooking(room.getId() , bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate() , bookingRequestDto.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventories);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequestDto.getRoomsCount()));
        log.info("total price {}" , totalPrice);
        Booking booking = Booking.builder()
                .roomsCount(bookingRequestDto.getRoomsCount())
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .status(BookingStatus.RESERVED)
                .user(getCurrentUser())
                .amount(totalPrice)
                .build();

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);

    }

    @Override
    @Transactional
    public BookingDto addGuest(Long bookingId, List<Long> guestIdList) {
        log.info("guestIDs {}" , guestIdList);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is already expired");
        }

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
                throw  new UnAuthorizedException("Booking does not belong to this user with id" + user.getId());
        }

        if(booking.getStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state");
        }

        for (Long guestId: guestIdList) {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: "+guestId));
            booking.getGuests().add(guest);
        }

        booking.setStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public String initiatePayment(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw  new UnAuthorizedException("Booking does not belong to this user with id" + user.getId());
        }

        if(booking.getStatus() != BookingStatus.GUEST_ADDED){
            throw new IllegalStateException(
                    "At least one guest must be added before proceeding with payment."
            );
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is already expired");
        }

        String sessionUrl =  checkoutService.getCheckoutSession(booking,
                frontEndUrl+"/payments/success" , frontEndUrl+"/payments/failure");
        booking.setStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        log.info(" event type {}" , event.getType());
        log.info("checking it matches or not {} " , "checkout.session.completed".equals(event.getType()));
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found for session id" + sessionId));

            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId() , booking.getCheckInDate(),
                    booking.getCheckOutDate() , booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId() , booking.getCheckInDate(),
                    booking.getCheckOutDate() , booking.getRoomsCount());

            log.info("Booking successfully done");

        }else {
            log.info("Unhandled event type {}" , event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw  new UnAuthorizedException("Booking does not belong to this user with id" + user.getId());
        }

        if(booking.getStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed booking can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId() , booking.getCheckInDate(),
                booking.getCheckOutDate() , booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId() , booking.getCheckInDate(),
                booking.getCheckOutDate() , booking.getRoomsCount());

//      Handling refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw  new UnAuthorizedException("Booking does not belong to this user with id" + user.getId());
        }

        return booking.getStatus().toString();
    }

    @Override
    public List<BookingDto> getAllBookingByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id is not found" + hotelId));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw  new UnAuthorizedException("Hotel does not belong to this user with id" + user.getId());
        }

        List<Booking> bookings = bookingRepository.findByHotel(hotel);
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user  = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream().map(bookingMapper::toDto).toList();
    }


    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }


}
