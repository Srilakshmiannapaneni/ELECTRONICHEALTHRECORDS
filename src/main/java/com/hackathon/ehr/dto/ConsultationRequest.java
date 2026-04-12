package com.hackathon.ehr.dto;

import com.hackathon.ehr.enums.ConsultationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationRequest {
    private Long patientId;
    private Long doctorId;
    private ConsultationType consultationType;
    private LocalDateTime scheduledAt;
    private String symptoms;
}
