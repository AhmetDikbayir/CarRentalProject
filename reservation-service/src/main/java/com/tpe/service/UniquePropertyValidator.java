package com.tpe.service;

import com.tpe.domain.Reservation;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.exceptions.ConflictException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {


    private final ReservationService reservationService;

    public void checkReservationStatus() {
    }
}
