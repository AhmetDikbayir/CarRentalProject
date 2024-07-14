package com.tpe.repository;


import com.tpe.domain.AppLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<AppLog,String> {
}
