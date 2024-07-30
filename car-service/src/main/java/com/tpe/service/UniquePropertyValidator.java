package com.tpe.service;

import com.tpe.domain.Car;
import com.tpe.payload.CarRequest;
import com.tpe.exceptions.ConflictException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final CarRepository carRepository;
    private final CarService carService;

    public void checkDuplicate(String numberPlate) {
        if (carRepository.existsByNumberPlate(numberPlate)) {
            throw new ConflictException(String.format(ErrorMessages.CAR_EXISTS_BY_THIS_NUMBERPLATE, numberPlate));
        }
    }

    public void checkUniqueProperties(Car car, CarRequest carRequest) {
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
