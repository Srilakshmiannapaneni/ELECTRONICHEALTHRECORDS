package com.hackathon.ehr.entity;

import com.hackathon.ehr.enums.RecordType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    private String title;
    private String description;
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "recorded_by", nullable = true) // The user who recorded it
    private User recordedBy;

    private LocalDateTime recordedAt = LocalDateTime.now();
}
