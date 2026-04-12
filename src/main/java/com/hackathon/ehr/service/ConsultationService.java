package com.hackathon.ehr.service;

import com.hackathon.ehr.entity.*;
import com.hackathon.ehr.enums.ConsultationStatus;
import com.hackathon.ehr.exception.BusinessRuleException;
import com.hackathon.ehr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationActivityLogRepository logRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public List<Consultation> getConsultationsForPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId);
    }

    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAllWithDetails();
    }

    @Transactional
    public Consultation bookConsultation(com.hackathon.ehr.dto.ConsultationRequest request) {
        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new BusinessRuleException("Patient not found"));
        
        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessRuleException("Doctor not found"));

        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setDoctor(doctor);
        consultation.setConsultationType(request.getConsultationType() != null
                ? request.getConsultationType()
                : com.hackathon.ehr.enums.ConsultationType.VIDEO);
        consultation.setScheduledAt(request.getScheduledAt() != null
                ? request.getScheduledAt()
                : LocalDateTime.now().plusMinutes(30));
        consultation.setSymptoms(request.getSymptoms());
        consultation.setStatus(ConsultationStatus.SCHEDULED);

        Consultation saved = consultationRepository.save(consultation);
        logActivity(saved, "BOOKED", null, ConsultationStatus.SCHEDULED, patient.getUser());
        return saved;
    }

    @Transactional
    public Consultation startConsultation(Long id, Long doctorId) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found"));
        
        if (c.getStatus() != ConsultationStatus.SCHEDULED) {
            throw new BusinessRuleException("Only SCHEDULED appointments can be started.");
        }
        
        if (!c.getDoctor().getId().equals(doctorId)) {
            throw new BusinessRuleException("Doctor does not match the booked appointment.");
        }

        c.setStatus(ConsultationStatus.IN_PROGRESS);
        c.setStartedAt(LocalDateTime.now());
        Consultation saved = consultationRepository.save(c);
        logActivity(saved, "STARTED", ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS, c.getDoctor().getUser());
        return saved;
    }

    @Transactional
    public Consultation completeConsultation(Long id, String diagnosis, String notes) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found"));
        
        if (c.getStatus() != ConsultationStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Only IN_PROGRESS appointments can be completed.");
        }
        
        c.setStatus(ConsultationStatus.COMPLETED);
        c.setDiagnosis(diagnosis);
        c.setNotes(notes);
        c.setEndedAt(LocalDateTime.now());
        Consultation saved = consultationRepository.save(c);
        logActivity(saved, "COMPLETED", ConsultationStatus.IN_PROGRESS, ConsultationStatus.COMPLETED, c.getDoctor().getUser());
        return saved;
    }

    @Transactional
    public Consultation cancelConsultation(Long id) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found"));
        
        if (c.getStatus() == ConsultationStatus.COMPLETED || c.getStatus() == ConsultationStatus.NO_SHOW) {
            throw new BusinessRuleException("Cannot cancel a completed or no-show appointment.");
        }
        
        ConsultationStatus previousStatus = c.getStatus();
        c.setStatus(ConsultationStatus.CANCELLED);
        Consultation saved = consultationRepository.save(c);
        logActivity(saved, "CANCELLED", previousStatus, ConsultationStatus.CANCELLED, c.getPatient().getUser());
        return saved;
    }

    @Transactional
    public Consultation markNoShow(Long id) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found"));
        
        if (c.getStatus() != ConsultationStatus.SCHEDULED) {
            throw new BusinessRuleException("Only SCHEDULED appointments can be marked as no-show.");
        }
        
        c.setStatus(ConsultationStatus.NO_SHOW);
        Consultation saved = consultationRepository.save(c);
        logActivity(saved, "NO_SHOW", ConsultationStatus.SCHEDULED, ConsultationStatus.NO_SHOW, c.getPatient().getUser());
        return saved;
    }

    public List<Consultation> getConsultationsForDoctor(Long doctorId) {
        return consultationRepository.findByDoctorId(doctorId);
    }

    private void logActivity(Consultation c, String action, ConsultationStatus oldS, ConsultationStatus newS, User u) {
        ConsultationActivityLog log = new ConsultationActivityLog();
        log.setConsultation(c);
        log.setAction(action);
        log.setOldStatus(oldS);
        log.setNewStatus(newS);
        log.setPerformedBy(u);
        logRepository.save(log);
    }
}
