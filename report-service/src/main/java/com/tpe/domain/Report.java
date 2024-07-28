package com.tpe.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;
    private LocalDateTime generatedAt;
    private String generatedBy; // The user or admin who generated the report

    private String fileName; // The name of the generated Excel file

    @Lob
    private byte[] content; // The actual report content in Excel format
}
