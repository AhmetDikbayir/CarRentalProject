package com.tpe.service;

import com.tpe.domain.Reservation;
import com.tpe.payload.messages.ResourceNotFoundException;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.exceptions.ConflictException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final ReservationRepository carRepository;
    private final ReservationService carService;
    private final ReservationRepository reservationRepository;


    public void checkReservationStatus(Long carId, LocalDateTime startReservationDateTime, LocalDateTime endReservationDateTime) {
        boolean exists = reservationRepository.existsByCarIdAndReservationStatusAndStartReservationDateTimeLessThanEqualAndEndReservationDateTimeGreaterThanEqual(
                carId, true, endReservationDateTime, startReservationDateTime);

        if (exists) {
            throw new ResourceNotFoundException(ErrorMessages.RESERVATION_NOT_AVAILABLE);
        }
    }

    public void checkUniqueProperties(Reservation reservation, ReservationRequest reservationRequest) {
    }
}
