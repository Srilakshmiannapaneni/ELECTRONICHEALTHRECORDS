package com.hackathon.ehr.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription_items")
@Data
@NoArgsConstructor
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Prescription prescription;

    @Column(nullable = false)
    private String medicineName;

    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
