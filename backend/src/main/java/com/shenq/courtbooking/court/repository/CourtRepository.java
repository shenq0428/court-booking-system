package com.shenq.courtbooking.court.repository;

import com.shenq.courtbooking.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court,Long>{
    List<Court>
    findAllByVenue_IdAndActiveTrueOrderByCourtNumberAsc(Long venueId);

    List<Court>
    findAllByVenue_IdInAndActiveTrue(List<Long> venueIds);
}
