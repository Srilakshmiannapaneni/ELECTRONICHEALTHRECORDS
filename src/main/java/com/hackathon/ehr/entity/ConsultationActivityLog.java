package com.hackathon.ehr.entity;

import com.hackathon.ehr.enums.ConsultationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultation_activity_logs")
@Data
@NoArgsConstructor
public class ConsultationActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    private String action;

    @ManyToOne
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus newStatus;

    private LocalDateTime timestamp = LocalDateTime.now();
}
