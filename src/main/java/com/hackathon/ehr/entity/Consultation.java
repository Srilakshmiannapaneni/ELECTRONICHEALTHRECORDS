package com.hackathon.ehr.entity;

import com.hackathon.ehr.enums.ConsultationStatus;
import com.hackathon.ehr.enums.ConsultationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status = ConsultationStatus.SCHEDULED;

    private String symptoms;
    private String diagnosis;
    private String notes;

    private Long prescriptionId;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Boolean followUpRequired;
}
