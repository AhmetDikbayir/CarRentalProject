package com.tpe.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.tpe.domain.Car;
import com.tpe.dto.AppLogRequest;
import com.tpe.dto.CarResponse;
import com.tpe.dto.CarRequest;
import com.tpe.enums.AppLogLevel;
import com.tpe.exceptions.ConflictException;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final UniquePropertyValidator uniquePropertyValidator;

    //Not: saveCar() *********************************************************************
    public void saveCar(CarRequest carRequest) {

        String numberPlate = carRequest.getNumberPlate();

        //  isCarExist
        uniquePropertyValidator.checkDuplicate(numberPlate);

        Car car = modelMapper.map(carRequest, Car.class);
        carRepository.save(car);


        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);

        String baseUrl = instanceInfo.getHomePageUrl(); // http://localhost:8083
        String path = "/log";
        String servicePath = baseUrl + path;   // http://localhost:8083/log

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription("Save a Car: " + car.getId());
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException("Log not created");
        }

        //logging loglama
        //log in giriş yapma
    }

    @Transactional
    public ResponseEntity<CarResponse> updateCar(CarRequest carRequest, Long carId) {

        //var mı kontrolü
        Car car = isCarExistsById(carId);
        //property validator
        uniquePropertyValidator.checkUniqueProperties(car, carRequest);

        Car updatedCar = modelMapper.map(carRequest, Car.class);
        updatedCar.setId(carId);

        carRepository.save(updatedCar);

        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);

        String baseUrl = instanceInfo.getHomePageUrl(); // http://localhost:8083
        String path = "/log";
        String servicePath = baseUrl + path;   // http://localhost:8083/log

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription("Car is updated by this id: " + car.getId());
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException(ErrorMessages.LOG_NOT_CREATED);
        }

        return ResponseEntity.ok(mapCarToCarDTO(updatedCar));
    }

    //Not: getAllCars() *********************************************************************
    public List<CarResponse> getAllCars() {

        List<Car> carList = carRepository.findAll();
        return carList.stream().map(this::mapCarToCarDTO).collect(Collectors.toList());
    }

    private CarResponse mapCarToCarDTO(Car car) {
        return modelMapper.map(car, CarResponse.class);
    }

    //Not: getById() ************************************************************************
    public CarResponse getById(Long id) {

        Car car = carRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.CAR_DOES_NOT_EXISTS_BY_ID, id)));

        CarResponse carResponse = mapCarToCarDTO(car);
        return carResponse;
    }


    // HELPER METHOD ****************************************************************************
    public Car isCarExistsById(Long carId) {

        return carRepository.findById(carId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.CAR_DOES_NOT_EXISTS_BY_ID, carId)));

    }



    //Not: deleteById() ************************************************************************
    public ResponseEntity<CarResponse> deleteCar(Long carId) {
    Car foundCar=isCarExistsById(carId);

    if (!foundCar.isAvailable()){
        throw new ConflictException(ErrorMessages.CAR_CANNOT_DELETED);
    }
    carRepository.deleteById(carId);


        InstanceInfo instanceInfo = eurekaClient.getApplication("log-service").getInstances().get(0);

        String baseUrl = instanceInfo.getHomePageUrl(); // http://localhost:8083
        String path = "/log";
        String servicePath = baseUrl + path;   // http://localhost:8083/log

        AppLogRequest appLogDTO = new AppLogRequest();
        appLogDTO.setLevel(AppLogLevel.INFO.name());
        appLogDTO.setDescription("Save a Car: " + foundCar.getId());
        appLogDTO.setTime(LocalDateTime.now());

        ResponseEntity<String> logResponse = restTemplate.postForEntity(servicePath, appLogDTO, String.class);

        if (!(logResponse.getStatusCode() == HttpStatus.CREATED)) {
            throw new ResourceNotFoundException("Log not created");
        }

        CarResponse carResponse=mapCarToCarDTO(foundCar);
        return new ResponseEntity<>(carResponse, HttpStatus.OK);
    }







}
