package com.example.repository;

import com.example.entity.PerformanceReport;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceReportRepository extends JpaRepository<PerformanceReport, Long> {
    Optional<PerformanceReport> findByStaff(User staff);
}
