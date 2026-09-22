package com.shenq.courtbooking.court.repository;

import com.shenq.courtbooking.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;


public interface CourtRepository extends JpaRepository<Court,Long>{
    List<Court>
    findAllByVenue_IdAndActiveTrueOrderByCourtNumberAsc(Long venueId);

    List<Court>
    findAllByVenue_IdInAndActiveTrue(List<Long> venueIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT court
            FROM Court court
            WHERE court.id = :courtId
            """)
    Optional<Court> findByIdForUpdate(
            @Param("courtId")
            Long courtId
    );
}
