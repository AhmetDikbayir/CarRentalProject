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

    private final ReservationRepository carRepository;
    private final ReservationService carService;

    public void checkDuplicate(String numberPlate) {
        if (carRepository.existsByNumberPlate(numberPlate)) {
            throw new ConflictException(String.format(ErrorMessages.CAR_EXISTS_BY_THIS_NUMBERPLATE, numberPlate));
        }
    }

    public void checkUniqueProperties(Reservation car, ReservationRequest carRequest) {
        String updatedNumberPlate = "";

        boolean isChanced = false;
        if (!car.getNumberPlate().equalsIgnoreCase(carRequest.getNumberPlate())) {
            updatedNumberPlate = carRequest.getNumberPlate();
            isChanced = true;
        }

        if (isChanced) {
            checkDuplicate(updatedNumberPlate);
        }


    }

}
