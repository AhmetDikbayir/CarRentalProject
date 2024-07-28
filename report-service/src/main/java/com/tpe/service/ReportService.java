package com.tpe.service;

import com.tpe.domain.Report;
import com.tpe.repository.ReportRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final RestTemplate restTemplate;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(RestTemplate restTemplate, ReportRepository reportRepository) {
        this.restTemplate = restTemplate;
        this.reportRepository = reportRepository;
    }

    public byte[] generateUserReport(String generatedBy) throws IOException {
        String userServiceUrl = "http://localhost:8081/users/all"; // Adjust based on actual endpoint
        List<Map<String, Object>> users = restTemplate.getForObject(userServiceUrl, List.class);
        byte[] reportContent = convertToExcel(users, "User Report");

        saveReport("User Report", reportContent, generatedBy, "user_report.xlsx");

        return reportContent;
    }

    public byte[] generateCarReport(String generatedBy) throws IOException {
        String carServiceUrl = "http://localhost:8082/cars/all"; // Adjust based on actual endpoint
        List<Map<String, Object>> cars = restTemplate.getForObject(carServiceUrl, List.class);
        byte[] reportContent = convertToExcel(cars, "Car Report");

        saveReport("Car Report", reportContent, generatedBy, "car_report.xlsx");

        return reportContent;
    }

    public byte[] generateReservationReport(String generatedBy) throws IOException {
        String reservationServiceUrl = "http://localhost:8083/reservations/all"; // Adjust based on actual endpoint
        List<Map<String, Object>> reservations = restTemplate.getForObject(reservationServiceUrl, List.class);
        byte[] reportContent = convertToExcel(reservations, "Reservation Report");

        saveReport("Reservation Report", reportContent, generatedBy, "reservation_report.xlsx");

        return reportContent;
    }

    private byte[] convertToExcel(List<Map<String, Object>> data, String sheetName) throws IOException {
        if (data == null || data.isEmpty()) {
            return new byte[0];
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Create headers
            Map<String, Object> firstRecord = data.get(0);
            Row headerRow = sheet.createRow(0);
            int headerCellIdx = 0;
            for (String key : firstRecord.keySet()) {
                Cell cell = headerRow.createCell(headerCellIdx++);
                cell.setCellValue(key);
            }

            // Create data rows
            int rowIdx = 1;
            for (Map<String, Object> record : data) {
                Row row = sheet.createRow(rowIdx++);
                int cellIdx = 0;
                for (Object value : record.values()) {
                    Cell cell = row.createCell(cellIdx++);
                    cell.setCellValue(value != null ? value.toString() : ""); // Handle null values
                }
            }

            // Write to byte array output stream
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }

    private void saveReport(String reportType, byte[] content, String generatedBy, String fileName) {
        Report report = new Report();
        report.setReportType(reportType);
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedBy(generatedBy);
        report.setFileName(fileName);
        report.setContent(content);
        reportRepository.save(report);
    }
}

