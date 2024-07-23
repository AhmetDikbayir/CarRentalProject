package com.tpe.payload.mappers;

import com.tpe.domain.Reservation;
import com.tpe.domain.User;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.payload.response.ReservationResponse;
import lombok.Builder;
import org.springframework.stereotype.Component;
@Component
public class ReservationMapper {

    private final User user;

    public ReservationMapper(User user) {
        this.user = user;
    }

    public ReservationResponse mapReservationToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .startReservationDateTime(reservation.getStartReservationDateTime())
                .endReservationDateTime(reservation.getEndReservationDateTime())
                .reservationStatus(reservation.getReservationStatus())
                .pricePerHour(reservation.getPricePerHour())
                .carId(reservation.getCar().getId())
                .build();
    }

    public Reservation mapReservationRequestToReservation(ReservationRequest reservationRequest) {

        return Reservation.builder()
                .startReservationDateTime(reservationRequest.getStartReservationDateTime())
                .endReservationDateTime(reservationRequest.getEndReservationDateTime())
                .user(user)
                .build();
    }
}