package com.hackathon.ehr.service;

import com.hackathon.ehr.entity.AccessLog;
import com.hackathon.ehr.entity.ConsultationActivityLog;
import com.hackathon.ehr.repository.AccessLogRepository;
import com.hackathon.ehr.repository.ConsultationActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AccessLogRepository accessLogRepository;
    private final ConsultationActivityLogRepository consultationActivityLogRepository;

    public List<AccessLog> getAccessLogs() {
        return accessLogRepository.findAll();
    }

    public List<ConsultationActivityLog> getConsultationActivityLogs() {
        return consultationActivityLogRepository.findAll();
    }
}
