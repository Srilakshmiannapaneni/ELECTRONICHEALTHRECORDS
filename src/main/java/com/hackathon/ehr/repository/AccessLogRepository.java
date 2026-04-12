package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByPatientId(Long patientId);
}
