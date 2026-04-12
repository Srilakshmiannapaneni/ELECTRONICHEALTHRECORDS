package com.hackathon.ehr.controller;

import com.hackathon.ehr.entity.AccessLog;
import com.hackathon.ehr.entity.ConsultationActivityLog;
import com.hackathon.ehr.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAuditLogs() {
        List<AccessLog> accessLogs = auditLogService.getAccessLogs();
        List<ConsultationActivityLog> consultationLogs = auditLogService.getConsultationActivityLogs();
        Map<String, Object> payload = new HashMap<>();
        payload.put("accessLogs", accessLogs);
        payload.put("consultationActivityLogs", consultationLogs);
        return ResponseEntity.ok(payload);
    }
}
