package com.hackathon.ehr.service;

import com.hackathon.ehr.dto.MedicalRecordRequest;
import com.hackathon.ehr.entity.AccessLog;
import com.hackathon.ehr.entity.MedicalRecord;
import com.hackathon.ehr.entity.PatientProfile;
import com.hackathon.ehr.entity.User;
import com.hackathon.ehr.enums.AccessType;
import com.hackathon.ehr.enums.RoleType;
import com.hackathon.ehr.exception.BusinessRuleException;
import com.hackathon.ehr.repository.AccessLogRepository;
import com.hackathon.ehr.repository.MedicalRecordRepository;
import com.hackathon.ehr.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AccessLogRepository accessLogRepository;

    @Transactional
    public MedicalRecord uploadMedicalRecord(MedicalRecordRequest request, MultipartFile file, User uploadedBy) {
        if (uploadedBy.getRole() != RoleType.DOCTOR && uploadedBy.getRole() != RoleType.ADMIN) {
            throw new BusinessRuleException("Only doctors or admin can upload medical records.");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Medical record file is required.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessRuleException("File size must be 10MB or less.");
        }

        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new BusinessRuleException("Patient not found."));

        try {
            Path uploadDirectory = Path.of("uploads");
            Files.createDirectories(uploadDirectory);
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path savedPath = uploadDirectory.resolve(filename);
            file.transferTo(savedPath);

            MedicalRecord record = new MedicalRecord();
            record.setPatient(patient);
            record.setRecordType(request.getRecordType());
            record.setTitle(request.getTitle());
            record.setDescription(request.getDescription());
            record.setFileUrl(savedPath.toString());
            record.setRecordedBy(uploadedBy);
            record.setRecordedAt(LocalDateTime.now());

            MedicalRecord saved = medicalRecordRepository.save(record);
            logAccess(patient, uploadedBy, AccessType.UPDATE);
            return saved;
        } catch (IOException ex) {
            throw new BusinessRuleException("Failed to save medical record file.");
        }
    }

    public List<MedicalRecord> getRecordsForPatient(Long patientId, User viewer) {
        PatientProfile patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new BusinessRuleException("Patient not found."));

        if (viewer.getRole() == RoleType.PATIENT && !patient.getUser().getId().equals(viewer.getId())) {
            throw new BusinessRuleException("Access denied to another patient's medical records.");
        }

        if (viewer.getRole() == RoleType.DOCTOR || viewer.getRole() == RoleType.ADMIN || viewer.getRole() == RoleType.PATIENT) {
            logAccess(patient, viewer, viewer.getRole() == RoleType.PATIENT ? AccessType.VIEW : AccessType.DOWNLOAD);
            return medicalRecordRepository.findByPatientId(patientId);
        }

        throw new BusinessRuleException("Role cannot access medical records.");
    }

    private void logAccess(PatientProfile patient, User accessedBy, AccessType accessType) {
        AccessLog accessLog = new AccessLog();
        accessLog.setPatient(patient);
        accessLog.setAccessedBy(accessedBy);
        accessLog.setAccessType(accessType);
        accessLog.setAccessedAt(LocalDateTime.now());
        accessLog.setIpAddress("UNKNOWN");
        accessLogRepository.save(accessLog);
    }
}
