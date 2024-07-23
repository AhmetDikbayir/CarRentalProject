package com.tpe.controller;

import com.tpe.payload.request.ReservationRequest;
import com.tpe.payload.response.ReservationResponse;
import com.tpe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    //todo total price hesaplanıp döndürülecek --- save-update reservation

    //Not: saveReservation() *********************************************************************
    @PostMapping // http://localhost:8085/reservations   + POST
    public ResponseEntity<Map<String, String>> saveReservation(@RequestBody @Valid ReservationRequest reservationRequest) {

        reservationService.saveReservation(reservationRequest);

        Map<String,String> map = new HashMap<>();
        map.put("message", "Reservation Successfully Saved");
        map.put("success", "true");

        return new ResponseEntity<>(map, HttpStatus.CREATED);

    }

    //Not: updateReservation() *********************************************************************
    @PutMapping("/updateCar") // http://localhost:8085/reservations/updateCar   + PUT
    public ResponseEntity<ReservationResponse> updateReservation(@RequestBody @Valid ReservationRequest reservationRequest, Long reservationId) {

        return reservationService.updateReservation(reservationRequest, reservationId);

    }
    //Not: getAllReservations() *********************************************************************
    @GetMapping("/allReservations") // http://localhost:8085/reservations/allReservations   + GET
    public ResponseEntity<List<ReservationResponse>> getAllReservations(){
        List<ReservationResponse> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    //Not: getById() ************************************************************************
    @GetMapping("/{reservationId}") // http://localhost:8085/reservations/1   + GET
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId) {
        ReservationResponse reservationResponse = reservationService.getById(reservationId);
       return ResponseEntity.ok(reservationResponse);
    }

    // getOwnReservationInformation *******************************
    @GetMapping("/{resId}")
    public ResponseEntity<ReservationResponse> getOwnReservationInformation(
            HttpServletRequest httpServletRequest,
            @PathVariable Long resId){
        return ResponseEntity.ok(reservationService
                .getOwnReservationInformation(httpServletRequest, resId));
    }


}
