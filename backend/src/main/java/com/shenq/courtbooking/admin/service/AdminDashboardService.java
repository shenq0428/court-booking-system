package com.shenq.courtbooking.admin.service;

import com.shenq.courtbooking.admin.dto.AdminDashboardResponse;
import com.shenq.courtbooking.booking.entity.BookingStatus;
import com.shenq.courtbooking.booking.repository.BookingRepository;
import com.shenq.courtbooking.court.repository.CourtRepository;
import com.shenq.courtbooking.payment.entity.PaymentStatus;
import com.shenq.courtbooking.payment.repository.PaymentRepository;
import com.shenq.courtbooking.venue.repository.VenueRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminDashboardService {
    private final VenueRepository venueRepository;
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(
        VenueRepository venueRepository,
        CourtRepository courtRepository,
        BookingRepository bookingRepository,
        PaymentRepository paymentRepository
    ){
        this.venueRepository=venueRepository;
        this.courtRepository=courtRepository;
        this.bookingRepository=bookingRepository;
        this.paymentRepository=paymentRepository;
    }

    @Transactional(readOnly=true)
    public AdminDashboardResponse getDashboard(){
        long totalVenues = venueRepository.count();
        long activeVenues = venueRepository.countByActiveTrue();
        long totalCourts = courtRepository.count();
        long activeCourts = courtRepository.countByActiveTrue();
        long pendingBookings = bookingRepository.countByStatus(
            BookingStatus.PENDING_PAYMENT
        );
        long confirmedBookings = bookingRepository.countByStatus(
            BookingStatus.CONFIRMED
        );
        long successfulPayments = paymentRepository.countByStatus(
            PaymentStatus.SUCCEEDED
        );
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus(
            PaymentStatus.SUCCEEDED
        );

        return new AdminDashboardResponse(
            totalVenues,
                activeVenues,
                totalCourts,
                activeCourts,
                pendingBookings,
                confirmedBookings,
                successfulPayments,
                totalRevenue
        );
    }   
}
