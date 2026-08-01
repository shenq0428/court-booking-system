package com.shenq.courtbooking.venue.repository;

import com.shenq.courtbooking.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}