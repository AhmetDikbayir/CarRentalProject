package com.tpe.controller;

import com.tpe.domain.Car;
import com.tpe.dto.CarResponse;
import com.tpe.dto.CarRequest;
import com.tpe.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    //Not: saveCar() *********************************************************************
    @PostMapping // http://localhost:8085/car   + POST
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> saveCar(@RequestBody @Valid CarRequest carRequest) {

        carService.saveCar(carRequest);

        Map<String,String> map = new HashMap<>();
        map.put("message", "Car Successfully Saved");
        map.put("success", "true");

        return new ResponseEntity<>(map, HttpStatus.CREATED);

    }

    //Not: updateCar() *********************************************************************
    @PutMapping("/updateCar") // http://localhost:8085/car/updateCar   + PUT
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CarResponse> updateCar(@RequestBody @Valid CarRequest carRequest, Long carId) {

        return carService.updateCar(carRequest, carId);

    }
    //Not:delete car() **************************************************************************
    @DeleteMapping("/deleteCar/{carId}") // http://localhost:8085/car/deleteCar/{carId}   + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CarResponse> deleteCar(@PathVariable Long carId){

        return carService.deleteCar(carId);
    }

    //Not:upload image file
    //Not:get all image file

    //Not: getAllCars() *********************************************************************
    @GetMapping("/allCars")
    //no pre authorize, everyone can get all cars.
    public ResponseEntity<List<CarResponse>> getAllCars(){
        List<CarResponse> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    //Not: getById() ************************************************************************
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CarResponse> getCar(@PathVariable Long id) {
       CarResponse carResponse = carService.getById(id);
       return ResponseEntity.ok(carResponse);
    }


}
