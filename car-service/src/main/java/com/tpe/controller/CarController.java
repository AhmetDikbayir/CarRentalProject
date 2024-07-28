package com.tpe.controller;

import com.tpe.payload.ImageResponse;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.response.CarResponse;
import com.tpe.payload.CarRequest;
import com.tpe.service.CarService;
import com.tpe.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final ImageService imageService;

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

    //get a image of the car
    @GetMapping("/{carId}/image")
    public ImageResponse getCarImage(@PathVariable Long carId) throws IOException {
        return carService.getFirstImage(carId);
    }

    //upload image file
    @PostMapping("/{carId}/image")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> addCarImage(@PathVariable Long carId, @RequestParam("image") MultipartFile image) throws IOException {
        carService.addImageToCar(carId, image);
        return ResponseEntity.ok(SuccessMessages.IMAGE_ADDED);
    }

    @PutMapping("/{carId}/image")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> updateCarImage(@PathVariable Long carId, @RequestParam("image") MultipartFile image) throws IOException {
        carService.updateCarImage(carId, image);
        return ResponseEntity.ok(SuccessMessages.IMAGE_UPDATED);
    }

    //get all images of the car
    @GetMapping("/{carId}/images")
    public List<ImageResponse> getCarImages(@PathVariable Long carId) throws IOException {
        return carService.getAllImages(carId);
    }

}
