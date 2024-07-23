package com.tpe.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.tpe.domain.Car;
import com.tpe.domain.Reservation;
import com.tpe.domain.User;
import com.tpe.dto.AppLogRequest;
import com.tpe.payload.mappers.ReservationMapper;
import com.tpe.payload.request.ReservationRequest;
import com.tpe.enums.AppLogLevel;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.response.ReservationResponse;
import com.tpe.repository.ReservationRepository;
import com.tpe.repository.UserRepository;
import com.tpe.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.its.asn1.Duration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;

    private final ReservationMapper reservationMapper;
    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final CarService carService;
    private final MethodHelper methodHelper;
    private final UniquePropertyValidator uniquePropertyValidator;

    //Not: saveReservation() *********************************************************************
    public ReservationResponse saveReservation(ReservationRequest reservationRequest) {
        List<Reservation> existingReservations = reservationRepository.findReservationsForCarInDateRange(
                reservationRequest.getCarId(),
                reservationRequest.getStartReservationDateTime(),
                reservationRequest.getEndReservationDateTime()
        );

        if (!existingReservations.isEmpty()) {
            throw new ResourceNotFoundException("Belirtilen tarihler arasında araç zaten rezerve edilmiş.");
        }

        Reservation reservation = reservationMapper.mapReservationRequestToReservation(reservationRequest);

        // Toplam fiyatı hesaplayın
        long hours = ChronoUnit.HOURS.between(reservation.getStartReservationDateTime(), reservation.getEndReservationDateTime());
        Double totalPrice = hours * reservation.getPricePerHour();
        reservation.setTotalPrice(totalPrice);

        
        reservationRepository.save(reservation);

        sendLog("Save a Reservation: " + reservation.getId());

        // Yanıt oluşturun
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setTotalPrice(totalPrice);
        return response;
    }



    @Transactional
    public ResponseEntity<ReservationResponse> updateReservation(ReservationRequest reservationRequest, Long reservationId) {

        // Var mı kontrolü
        Reservation existingReservation = reservationRepository.findById(reservationId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, reservationId)));

        // Rezervasyon tarih aralığında çakışma kontrolü
        boolean isAvailable = checkReservationStatus(reservationRequest.getCarId(),
                reservationRequest.getStartReservationDateTime(),
                reservationRequest.getEndReservationDateTime());

        if (!isAvailable) {
            throw new ResourceNotFoundException("Belirtilen tarihler arasında araç zaten rezerve edilmiş.");
        }

        // Car ve User nesnelerini alın
        Car car = carService.isCarExistsById(reservationRequest.getCarId());
        User user = methodHelper.isUserExist(reservationRequest.getUserId());

        // Rezervasyonu güncelle
        existingReservation.setStartReservationDateTime(reservationRequest.getStartReservationDateTime());
        existingReservation.setEndReservationDateTime(reservationRequest.getEndReservationDateTime());
        existingReservation.setCar(car);
        existingReservation.setUser(user);

        // Toplam fiyatı hesaplayın
        long hours = ChronoUnit.HOURS.between(existingReservation.getStartReservationDateTime(), existingReservation.getEndReservationDateTime());
        Double totalPrice = hours * existingReservation.getPricePerHour();
        existingReservation.setTotalPrice(totalPrice);

        reservationRepository.save(existingReservation);

        sendLog("Update a Reservation: " + existingReservation.getId());

        return ResponseEntity.ok(reservationMapper.mapReservationToReservationResponse(existingReservation));
    }




    //Not: getAllReservations() *********************************************************************
    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream()
                .map(reservationMapper::mapReservationToReservationResponse)
                .collect(Collectors.toList());
    }



    //Not: getById() ************************************************************************
    public ReservationResponse getById(Long id, String username) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, id)));

        if (!reservation.getUser().getEmail().equals(username)) {
            try {
                throw new AccessDeniedException("Bu rezervasyona erişim izniniz yok.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        return reservationMapper.mapReservationToReservationResponse(reservation);
    }


    //Not: deleteReservation() ************************************************************************
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, id)));

        reservationRepository.delete(reservation);

        sendLog("Reservation deleted: " + reservation.getId());
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
    return null;
    }

    private void sendLog(String description) {
        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);
        String baseUrl = instanceInfo.getHomePageUrl();
        String path = "/log";
        String servicePath = baseUrl + path;

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription(description);
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException("Log not created");
        }
    }

}
