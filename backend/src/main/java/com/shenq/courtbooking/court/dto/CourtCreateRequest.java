package com.shenq.courtbooking.court.dto;

import com.shenq.courtbooking.court.entity.SportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CourtCreateRequest(

        @NotNull
        Long venueId,

        @NotNull
        @Min(1)
        Integer courtNumber,

        @NotNull
        SportType sport,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal pricePerHour

) {
}