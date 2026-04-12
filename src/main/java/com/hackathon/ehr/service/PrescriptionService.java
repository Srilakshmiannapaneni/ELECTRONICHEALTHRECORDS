package com.hackathon.ehr.service;

import com.hackathon.ehr.dto.PrescriptionItemDTO;
import com.hackathon.ehr.dto.PrescriptionRequest;
import com.hackathon.ehr.entity.Consultation;
import com.hackathon.ehr.entity.Prescription;
import com.hackathon.ehr.entity.PrescriptionItem;
import com.hackathon.ehr.enums.ConsultationStatus;
import com.hackathon.ehr.enums.PrescriptionStatus;
import com.hackathon.ehr.exception.BusinessRuleException;
import com.hackathon.ehr.repository.ConsultationRepository;
import com.hackathon.ehr.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    
    @Transactional
    public Prescription issuePrescription(PrescriptionRequest request) {
        Consultation c = consultationRepository.findByIdWithDetails(request.getConsultationId())
                .orElseThrow(() -> new BusinessRuleException("Appointment not found"));
            
        if (c.getStatus() != ConsultationStatus.COMPLETED) {
            throw new BusinessRuleException("Prescription issued only after COMPLETED appointments.");
        }
        
        if (request.getDigitalSignature() == null || request.getDigitalSignature().isBlank()) {
            throw new BusinessRuleException("Digital signature required for prescription validity.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessRuleException("Prescription must include at least one medicine item.");
        }

        Prescription prescription = new Prescription();
        prescription.setConsultation(c);
        prescription.setPatient(c.getPatient());
        prescription.setDoctor(c.getDoctor());
        prescription.setIssuedAt(LocalDateTime.now());
        prescription.setValidUntil(LocalDateTime.now().plusDays(30));
        prescription.setDigitalSignature(request.getDigitalSignature());
        prescription.setStatus(PrescriptionStatus.ACTIVE);

        List<PrescriptionItem> items = request.getItems().stream().map(this::toEntity).collect(Collectors.toList());
        items.forEach(item -> item.setPrescription(prescription));
        prescription.setItems(items);

        Prescription saved = prescriptionRepository.save(prescription);
        c.setPrescriptionId(saved.getId());
        consultationRepository.save(c);
        return saved;
    }

    private PrescriptionItem toEntity(PrescriptionItemDTO dto) {
        if (dto.getMedicineName() == null || dto.getMedicineName().isBlank()) {
            throw new BusinessRuleException("Medicine name is required.");
        }
        PrescriptionItem item = new PrescriptionItem();
        item.setMedicineName(dto.getMedicineName());
        item.setDosage(dto.getDosage());
        item.setFrequency(dto.getFrequency());
        item.setDuration(dto.getDuration());
        item.setInstructions(dto.getInstructions());
        return item;
    }

    public List<Prescription> getPatientPrescriptions(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAllWithDetails();
    }

    @Transactional
    public Prescription dispensePrescription(Long id) {
        Prescription p = prescriptionRepository.findById(id).orElseThrow(() -> new BusinessRuleException("Prescription not found."));
        if (p.getStatus() != PrescriptionStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE prescriptions can be dispensed.");
        }
        if (LocalDateTime.now().isAfter(p.getValidUntil())) {
            p.setStatus(PrescriptionStatus.EXPIRED);
            prescriptionRepository.save(p);
            throw new BusinessRuleException("Cannot dispense EXPIRED prescription.");
        }
        p.setStatus(PrescriptionStatus.DISPENSED);
        return prescriptionRepository.save(p);
    }

    @Transactional
    public Prescription rejectPrescription(Long id) {
        Prescription p = prescriptionRepository.findById(id).orElseThrow(() -> new BusinessRuleException("Prescription not found."));
        if (p.getStatus() != PrescriptionStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE prescriptions can be rejected.");
        }
        p.setStatus(PrescriptionStatus.CANCELLED);
        return prescriptionRepository.save(p);
    }
}
