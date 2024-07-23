package com.tpe.controller;

import com.tpe.payload.request.ReservationRequest;
import com.tpe.payload.response.ReservationResponse;
import com.tpe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/updateCar") // http://localhost:8085/car   + POST
    public ResponseEntity<ReservationResponse> updateReservation(@RequestBody @Valid ReservationRequest reservationRequest, Long reservationId) {

        return reservationService.updateReservation(reservationRequest, reservationId);

    }
    //Not: getAllReservations() *********************************************************************
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllCars(){
        List<ReservationResponse> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    //Not: getById() ************************************************************************
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        ReservationResponse reservationResponse = reservationService.getById(id);
       return ResponseEntity.ok(reservationResponse);
    }


}
