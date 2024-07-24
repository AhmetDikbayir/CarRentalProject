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

import com.tpe.service.helper.ReservationMethodHelper;
import com.tpe.service.helper.UserMethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;
    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final CarService carService;
    private final ReservationMethodHelper reservationMethodHelper;
    private final UserMethodHelper userMethodHelper;

    private final UniquePropertyValidator uniquePropertyValidator;

    private final UserService userService;

    //Not: saveReservation() *********************************************************************
    public ReservationResponse saveReservation(ReservationRequest reservationRequest) {
        // Tarihlerin geçerliliğini kontrol et
        if (reservationRequest.getStartReservationDateTime().isAfter(reservationRequest.getEndReservationDateTime())) {
            throw new IllegalArgumentException("Başlangıç tarihi bitiş tarihinden sonra olamaz.");
        }

        // Tarihlerdeki çakışmayı kontrol et
        List<Reservation> existingReservations = reservationRepository.findReservationsForCarInDateRange(
                reservationRequest.getCar().getId(),
                reservationRequest.getStartReservationDateTime(),
                reservationRequest.getEndReservationDateTime()
        );

        if (!existingReservations.isEmpty()) {
            throw new ResourceNotFoundException("Belirtilen tarihler arasında araç zaten rezerve edilmiş.");
        }

        // Araç ve kullanıcı kontrolü
        Car car = carService.isCarExistsById(reservationRequest.getCar().getId());


        // Rezervasyonu oluştur
        Reservation reservation = reservationMapper.mapReservationRequestToReservation(reservationRequest);
        reservation.setCar(car);
        reservation.setUser(userMethodHelper.isUserExist(reservationRequest.getUser().getId()));

        // Toplam fiyatı hesapla
        long hours = ChronoUnit.HOURS.between(reservation.getStartReservationDateTime(), reservation.getEndReservationDateTime());
        Double totalPrice = hours * reservation.getPricePerHour();
        reservation.setTotalPrice(totalPrice);

        // Rezervasyonu kaydet
        reservationRepository.save(reservation);

        sendLog("Save a Reservation: " + reservation.getId());

        // Yanıt oluştur
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setTotalPrice(totalPrice);
        return response;
    }

    @Transactional
    public ResponseEntity<ReservationResponse> updateReservation(ReservationRequest reservationRequest, Long reservationId) {
        // Rezervasyonun mevcut olup olmadığını kontrol et
        Reservation existingReservation = reservationRepository.findById(reservationId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, reservationId)));

        // Tarihlerin geçerliliğini kontrol et
        if (reservationRequest.getStartReservationDateTime().isAfter(reservationRequest.getEndReservationDateTime())) {
            throw new IllegalArgumentException("Başlangıç tarihi bitiş tarihinden sonra olamaz.");
        }

        // Tarihlerdeki çakışmayı kontrol et
        boolean isAvailable = checkReservationStatus(reservationRequest.getCar().getId(),
                reservationRequest.getStartReservationDateTime(),
                reservationRequest.getEndReservationDateTime());

        if (!isAvailable) {
            throw new ResourceNotFoundException("Belirtilen tarihler arasında araç zaten rezerve edilmiş.");
        }

        // Car ve User nesnelerini al
        Car car = carService.isCarExistsById(reservationRequest.getCar().getId());
        User user = userMethodHelper.isUserExist(reservationRequest.getUser().getId());

        // Rezervasyonu güncelle
        existingReservation.setStartReservationDateTime(reservationRequest.getStartReservationDateTime());
        existingReservation.setEndReservationDateTime(reservationRequest.getEndReservationDateTime());
        existingReservation.setCar(car);
        existingReservation.setUser(user);

        // Toplam fiyatı hesapla
        long hours = ChronoUnit.HOURS.between(existingReservation.getStartReservationDateTime(), existingReservation.getEndReservationDateTime());
        Double totalPrice = hours * existingReservation.getPricePerHour();
        existingReservation.setTotalPrice(totalPrice);

        // Rezervasyonu kaydet
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
    //Admin idsi verilen reservation bilgilerine ulaşıyor.
    public ReservationResponse getById(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, reservationId)));
        return reservationMapper.mapReservationToReservationResponse(reservation);
    }

    //User sadece kendi Reservation biligilerine ulaşıyor..
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
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException(String.format(ErrorMessages.RESERVATION_DOES_NOT_EXISTS_BY_ID, id)));
            reservationRepository.delete(reservation);
        sendLog("Reservation deleted: " + reservation.getId());
    }

    public boolean checkReservationStatus(Long reservationId, LocalDateTime startDateTime, LocalDateTime
            endDateTime) {
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
    }

    public ReservationResponse getOwnReservationInformation(
            HttpServletRequest httpServletRequest, Long resId) {
        String email = (String) httpServletRequest.getAttribute("username");
        User user = userMethodHelper.isUserExistByEmail(email);
        Reservation ownReservation = reservationMethodHelper.isReservationExistsById(resId);
        for (Reservation reservation : user.getReservationList()) {
            if (resId.equals(reservation.getId())) {
                return reservationMapper.mapReservationToReservationResponse(reservation);
            }
        }
        return null;
    }
}