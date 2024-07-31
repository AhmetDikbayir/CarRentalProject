package com.tpe.controller;

import com.tpe.domain.AppLog;
import com.tpe.payload.request.AppLogRequest;
import com.tpe.enums.AppLogLevel;
import com.tpe.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    //http://localhost:8083/log + POST + JSON body
    @PostMapping
    public ResponseEntity<String> createLog(@RequestBody AppLogRequest appLogRequest) {

        AppLog appLog = new AppLog();

        appLog.setLevel(AppLogLevel.fromStringToEnum(appLogRequest.getLevel()));
        appLog.setDescription(appLogRequest.getDescription());
        appLog.setTime(appLogRequest.getTime());

        logService.saveLog(appLog);

        return new ResponseEntity<>(appLog.getId(), HttpStatus.CREATED);//200
    }
}
