package com.shenq.courtbooking.venue.dto;

import com.shenq.courtbooking.court.entity.SportType;

import java.math.BigDecimal;
import java.util.List;

public record VenueSummaryResponse(
    Long id,
    String name,
    String address,
    List<SportType> sports,
    BigDecimal startingPricePerHour,
    String imageUrl
){}


