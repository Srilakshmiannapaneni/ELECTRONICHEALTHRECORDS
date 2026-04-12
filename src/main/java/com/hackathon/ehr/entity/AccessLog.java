package com.hackathon.ehr.entity;

import com.hackathon.ehr.enums.AccessType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
@Data
@NoArgsConstructor
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "accessed_by", nullable = false)
    private User accessedBy;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    private LocalDateTime accessedAt = LocalDateTime.now();

    private String ipAddress;
}
