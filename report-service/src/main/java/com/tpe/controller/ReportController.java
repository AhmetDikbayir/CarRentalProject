package com.tpe.controller;

import com.tpe.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping ("/users")
    public ResponseEntity<String> generateUserReport() {
        String report = reportService.generateUserReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/cars")
    public ResponseEntity<String> generateCarReport() {
        String report = reportService.generateCarReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/reservations")
    public ResponseEntity<String> generateReservationReport() {
        String report = reportService.generateReservationReport();
        return ResponseEntity.ok(report);
    }
}


