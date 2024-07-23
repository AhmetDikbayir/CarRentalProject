package com.tpe.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType; // e.g., "user_report", "car_report", "reservation_report"

    private String status; // e.g., "pending", "generated", "failed"

    private LocalDateTime generatedAt;


}
