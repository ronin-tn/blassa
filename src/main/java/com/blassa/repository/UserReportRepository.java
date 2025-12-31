package com.blassa.repository;

import com.blassa.model.entity.UserReport;
import com.blassa.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<UserReport> findAllByOrderByCreatedAtDesc();
}
