package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByConsultationId(Long consultationId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM Prescription p JOIN FETCH p.items JOIN FETCH p.patient pp JOIN FETCH pp.user JOIN FETCH p.doctor d JOIN FETCH d.user")
    List<Prescription> findAllWithDetails();
}
