package com.shenq.courtbooking.venue.repository;

import com.shenq.courtbooking.venue.entity.Venue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shenq.courtbooking.court.entity.SportType;

import java.math.BigDecimal;
import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Page<Venue> findAllByActiveTrue(Pageable pageable);

    @Query(value = """
            SELECT venue.*
            FROM venues venue
            WHERE venue.active = true
              AND venue.id <> :venueId
              AND venue.latitude IS NOT NULL
              AND venue.longitude IS NOT NULL
            ORDER BY (
                6371 * ACOS(
                    LEAST(
                        1.0,
                        GREATEST(
                            -1.0,
                            COS(RADIANS(CAST(:latitude AS double precision)))
                            * COS(RADIANS(CAST(venue.latitude AS double precision)))
                            * COS(
                                RADIANS(CAST(venue.longitude AS double precision))
                                - RADIANS(CAST(:longitude AS double precision))
                            )
                            + SIN(RADIANS(CAST(:latitude AS double precision)))
                            * SIN(RADIANS(CAST(venue.latitude AS double precision)))
                        )
                    )
                )
            )
            LIMIT :limit
            """, nativeQuery = true)
    List<Venue> findNearbyByCoordinates(
            @Param("venueId") Long venueId,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("limit") int limit);

    List<Venue> findByActiveTrueAndIdNotAndCityIgnoreCaseOrderByNameAsc(
            Long venueId,
            String city,
            Pageable pageable);

    @Query(value = """
            SELECT venue
            FROM Venue venue
            WHERE venue.active = true
              AND EXISTS (
                  SELECT court.id
                  FROM Court court
                  WHERE court.venue = venue
                    AND court.active = true
              )
            """, countQuery = """
            SELECT COUNT(venue.id)
            FROM Venue venue
            WHERE venue.active = true
              AND EXISTS (
                  SELECT court.id
                  FROM Court court
                  WHERE court.venue = venue
                    AND court.active = true
              )
            """)
    Page<Venue> findAllBookable(Pageable pageable);

    @Query(value = """
            SELECT venue
            FROM Venue venue
            WHERE venue.active = true
              AND (
                  :location = ''
                  OR LOWER(venue.name)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.city)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.state)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.addressLine1)
                     LIKE LOWER(CONCAT('%', :location, '%'))
              )
              AND EXISTS (
                  SELECT court.id
                  FROM Court court
                  WHERE court.venue = venue
                    AND court.active = true
                    AND (
                        :sport IS NULL
                        OR court.sport = :sport
                    )
              )
            """, 
            countQuery = """
            SELECT COUNT(venue.id)
            FROM Venue venue
            WHERE venue.active = true
              AND (
                  :location = ''
                  OR LOWER(venue.name)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.city)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.state)
                     LIKE LOWER(CONCAT('%', :location, '%'))
                  OR LOWER(venue.addressLine1)
                     LIKE LOWER(CONCAT('%', :location, '%'))
              )
              AND EXISTS (
                  SELECT court.id
                  FROM Court court
                  WHERE court.venue = venue
                    AND court.active = true
                    AND (
                        :sport IS NULL
                        OR court.sport = :sport
                    )
              )
            """)
    Page<Venue> searchActiveVenues(
            @Param("location") String location,
            @Param("sport") SportType sport,
            Pageable pageable);
}