package com.tpe.repository;

import com.tpe.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.carId = :carId AND " +
            "(r.startReservationDateTime <= :endDateTime AND r.endReservationDateTime >= :startDateTime)")
    List<Reservation> findReservationsForCarInDateRange(@Param("carId") Long carId,
                                                        @Param("startDateTime") LocalDateTime startDateTime,
                                                        @Param("endDateTime") LocalDateTime endDateTime);

    boolean existsByCarIdAndReservationStatusAndStartReservationDateTimeLessThanEqualAndEndReservationDateTimeGreaterThanEqual(
            Long carId, Boolean reservationStatus, LocalDateTime endDateTime, LocalDateTime startDateTime);

}
