package com.tpe.repository;

import com.tpe.domain.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Long, Reports> {
}
