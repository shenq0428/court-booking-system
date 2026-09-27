package com.shenq.courtbooking.admin.dto;


import java.math.BigDecimal;

public record AdminDashboardResponse (
    long totalVenues,
    long activeVenues,
    long totalCourts,
    long activeCourts,
    long pendingBookings,
    long confirmedBookings,
    long successfullPayments,
    BigDecimal totalRevenue
){
}
