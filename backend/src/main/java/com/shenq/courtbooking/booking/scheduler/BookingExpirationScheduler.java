package com.shenq.courtbooking.booking.scheduler;

import com.shenq.courtbooking.booking.service.BookingService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingExpirationScheduler{
    private final BookingService bookingService;

    public BookingExpirationScheduler(
        BookingService bookingService
    ){
        this.bookingService = bookingService;
    }

    @Scheduled(fixedDelay = 60_000)
    public void expirePendingBookings(){
        bookingService.expireOverduePendingBookings();
    }
}
