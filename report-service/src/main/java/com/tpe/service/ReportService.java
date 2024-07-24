package com.tpe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class ReportService {

    private final RestTemplate restTemplate;

    public ReportService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateUserReport() {
        String userServiceUrl = "http://localhost:8081/users/all"; // Adjust based on actual endpoint
        List<Object> users = restTemplate.getForObject(userServiceUrl, List.class);
        // Logic to generate report (e.g., CSV or JSON)
        return convertToCSV(users);
    }

    public String generateCarReport() {
        String carServiceUrl = "http://localhost:8082/cars/all"; // Adjust based on actual endpoint
        List<Object> cars = restTemplate.getForObject(carServiceUrl, List.class);
        // Logic to generate report (e.g., CSV or JSON)
        return convertToCSV(cars);
    }

    public String generateReservationReport() {
        String reservationServiceUrl = "http://localhost:8083/reservations/all"; // Adjust based on actual endpoint
        List<Object> reservations = restTemplate.getForObject(reservationServiceUrl, List.class);
        // Logic to generate report (e.g., CSV or JSON)
        return convertToCSV(reservations);
    }

    private String convertToCSV(List<Object> data) {
        // Convert data to CSV format
        StringBuilder csvBuilder = new StringBuilder();
        for (Object obj : data) {
            csvBuilder.append(obj.toString()).append("\n");
        }
        return csvBuilder.toString();
    }
}

