package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.ConsultationActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationActivityLogRepository extends JpaRepository<ConsultationActivityLog, Long> {
    List<ConsultationActivityLog> findByConsultationId(Long consultationId);
}
