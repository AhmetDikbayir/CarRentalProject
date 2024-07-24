package com.tpe.service;

import com.tpe.domain.Reservation;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.exceptions.ConflictException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {



    private final ReservationService reservationService;

    private final ReservationRepository carRepository;
    private final ReservationService carService;
    private final ReservationRepository reservationRepository;


    public void checkReservationStatus(Long carId, LocalDateTime startReservationDateTime, LocalDateTime endReservationDateTime) {
        List<Reservation> foundReservations = reservationRepository.findReservationsForCarInDateRange(
                carId, endReservationDateTime, startReservationDateTime);

        if (foundReservations.size()!=0) {
            throw new ResourceNotFoundException(ErrorMessages.RESERVATION_NOT_AVAILABLE);
        }
    }


    public void checkUniqueProperties(Reservation reservation, ReservationRequest reservationRequest) {
    }
}
