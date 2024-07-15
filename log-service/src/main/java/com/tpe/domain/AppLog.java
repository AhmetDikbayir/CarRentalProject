package com.tpe.domain;

import com.tpe.enums.AppLogLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter

@Document
public class AppLog {

    @Id
    private String id;

    private AppLogLevel level;

    private String description;

    private LocalDateTime time;

}
