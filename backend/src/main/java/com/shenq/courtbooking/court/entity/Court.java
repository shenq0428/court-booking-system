package com.shenq.courtbooking.court.entity;

import com.shenq.courtbooking.venue.entity.Venue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(
        name = "courts",
        uniqueConstraints = 
        {
                @UniqueConstraint
                (
                        name = "uk_court_venue_number",
                        columnNames = {"venue_id","court_number"}
                )
        }
)
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn( name = "venue_id",nullable = false)
    private Venue venue;

    @Column(name = "court_number", nullable = false)
    private int courtNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SportType sport;

    @Column(
            name = "price_per_hour",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    private boolean active = true;

    public Court() {
    }

    public Court(
            Venue venue,
            int courtNumber,
            SportType sport,
            BigDecimal pricePerHour
    ) {
        this.venue = venue;
        this.courtNumber = courtNumber;
        this.sport = sport;
        this.pricePerHour = pricePerHour;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public Venue getVenue() {
        return venue;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public SportType getSport() {
        return sport;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public boolean isActive() {
        return active;
    }

    public void setSport(SportType sport) {
        this.sport = sport;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}