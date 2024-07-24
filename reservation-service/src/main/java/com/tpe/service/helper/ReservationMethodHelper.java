package com.tpe.service.helper;

import com.tpe.domain.Reservation;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.ReservationRepository;
import com.tpe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMethodHelper {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    public Reservation isReservationExistsById(Long reservationId){
        return reservationRepository.findById(reservationId).orElseThrow(
                ()->new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID + reservationId))
        );

    }
}
