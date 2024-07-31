package com.tpe.controller;

import com.tpe.payload.request.ReservationRequest;
import com.tpe.payload.response.ReservationResponse;
import com.tpe.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
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
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<Map<String, Object>> saveReservation(@RequestBody @Valid ReservationRequest reservationRequest) {

        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reservation Successfully Saved");
        response.put("success", true);
        response.put("reservationId", reservationResponse.getId());
        response.put("totalPrice", reservationResponse.getTotalPrice());

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    //Not: updateReservation() *********************************************************************

    @PutMapping("/reservationId") // http://localhost:8085/updateReservation   + POST
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")    //??????????? müşteri de rezervasyonunu güncellemeli değil mi?
    public ResponseEntity<ReservationResponse> updateReservation(@RequestBody @Valid ReservationRequest reservationRequest, Long reservationId) {

        return reservationService.updateReservation(reservationRequest, reservationId);

    }
    //Not: getAllReservations() *********************************************************************

    @GetMapping("/allReservations") // http://localhost:8085/reservations/allReservations   + GET
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getAllReservations(){
        List<ReservationResponse> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    //Not: getById() ************************************************************************

    @GetMapping("/{reservationId}") // http://localhost:8085/reservations/1   + GET
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId) {
        ReservationResponse reservationResponse = reservationService.getById(reservationId);
       return ResponseEntity.ok(reservationResponse);
    }

    // getOwnReservationInformation *******************************
    @GetMapping("/{resId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<ReservationResponse> getOwnReservationInformation(
            HttpServletRequest httpServletRequest,
            @PathVariable Long resId){
        return ResponseEntity.ok(reservationService
                .getOwnReservationInformation(httpServletRequest, resId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        ReservationResponse reservationResponse = reservationService.getById(id, username);
        return ResponseEntity.ok(reservationResponse);
    }


    //Giriş yapan kullanıcı kendi rezervasyonlarını görebilir...
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id, Authentication authentication) {
        // Kimlik doğrulamasından kullanıcının kimliğini al
        String username = authentication.getName(); // Kullanıcı adını al

        // Rezervasyonu getirin ve kullanıcı doğrulaması yapın
        ReservationResponse reservationResponse = reservationService.getById(id, username);
        return ResponseEntity.ok(reservationResponse);
    }


    //Not: deleteReservation() ************************************************************************
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reservation Successfully Deleted");
        response.put("success", "true");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
