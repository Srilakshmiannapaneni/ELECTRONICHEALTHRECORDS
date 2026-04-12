package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientId(Long patientId);
    List<Consultation> findByDoctorId(Long doctorId);

    // Required: At least one JOIN query (fetches consultation with patient and doctor details)
    @Query("SELECT c FROM Consultation c JOIN FETCH c.patient p JOIN FETCH p.user JOIN FETCH c.doctor d JOIN FETCH d.user WHERE c.id = :id")
    Optional<Consultation> findByIdWithDetails(@Param("id") Long id);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Consultation c JOIN FETCH c.patient pp JOIN FETCH pp.user JOIN FETCH c.doctor d JOIN FETCH d.user")
    List<Consultation> findAllWithDetails();

    // Required: At least one aggregate query (Total consultations per doctor)
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.doctor.id = :doctorId AND c.status = 'COMPLETED'")
    Long countCompletedConsultationsByDoctor(@Param("doctorId") Long doctorId);
}
