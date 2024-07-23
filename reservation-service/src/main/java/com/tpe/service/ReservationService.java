package com.tpe.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.tpe.domain.Reservation;
import com.tpe.domain.User;
import com.tpe.dto.AppLogRequest;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.enums.AppLogLevel;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.response.ReservationResponse;
import com.tpe.repository.ReservationRepository;
import com.tpe.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ModelMapper modelMapper;
    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final MethodHelper methodHelper;

    private final UserService userService;

    //Not: saveReservation() *********************************************************************
    public void saveReservation(ReservationRequest reservationRequest) {


        //  isReservationStatus
        uniquePropertyValidator.checkReservationStatus();

        Reservation reservation = modelMapper.map(reservationRequest, Reservation.class);
        reservationRepository.save(reservation);


        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);

        String baseUrl = instanceInfo.getHomePageUrl(); // http://localhost:8083
        String path = "/log";
        String servicePath = baseUrl + path;   // http://localhost:8083/log

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription("Save a Reservation: " + reservation.getId());
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException("Log not created");
        }

    }

    @Transactional
    public ResponseEntity<ReservationResponse> updateReservation(ReservationRequest reservationRequest, Long reservationId) {

        //var mı kontrolü
        Reservation reservation = checkReservationStatus(reservationId);
        //property validator
        uniquePropertyValidator.checkUniqueProperties(reservation, reservationRequest);

        Reservation updatedReservation = modelMapper.map(reservationRequest, Reservation.class);
        updatedReservation.setId(reservationId);

        reservationRepository.save(updatedReservation);

        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);

        String baseUrl = instanceInfo.getHomePageUrl(); // http://localhost:8083
        String path = "/log";
        String servicePath = baseUrl + path;   // http://localhost:8083/log

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription("Reservation is updated by this id: " + reservation.getId());
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException(ErrorMessages.LOG_NOT_CREATED);
        }

        return ResponseEntity.ok(mapReservationToReservationDTO(updatedReservation));
    }



    //Not: getAllReservations() *********************************************************************
    public List<ReservationResponse> getAllReservations() {

        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream().map(this::mapReservationToReservationDTO).collect(Collectors.toList());
    }

    private ReservationResponse mapReservationToReservationDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationResponse.class);
    }

    //Not: getById() ************************************************************************
    public ReservationResponse getById(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, reservationId)));

        ReservationResponse reservationResponse = mapReservationToReservationDTO(reservation);
        return reservationResponse;
    }

    public boolean checkReservationStatus(Long reservationId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Reservation> reservations = reservationRepository.findReservationsForCarInDateRange(reservationId, startDateTime, endDateTime);

        if (reservations.isEmpty()) {
            return true; // car is available for reservation
        } else {
            throw new ResourceNotFoundException(ErrorMessages.RESERVATION_NOT_AVAILABLE);
        }
    }

    private Reservation checkReservationStatus(Long reservationId) {
    }


    public ReservationResponse getOwnReservationInformation(
            HttpServletRequest httpServletRequest, Long resId) {
        String email = (String) httpServletRequest.getAttribute("username");
        User user = userService.
        methodHelper.isReservationExistsById(resId);
    }
}
