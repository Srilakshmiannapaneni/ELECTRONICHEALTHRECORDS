package com.hackathon.ehr.controller;

import com.hackathon.ehr.dto.MedicalRecordRequest;
import com.hackathon.ehr.entity.MedicalRecord;
import com.hackathon.ehr.security.UserPrincipal;
import com.hackathon.ehr.service.MedicalRecordService;
import com.hackathon.ehr.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MedicalRecord> uploadMedicalRecord(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long patientId,
            @RequestParam String recordType,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestPart("file") MultipartFile file) {
        MedicalRecordRequest request = new MedicalRecordRequest();
        request.setPatientId(patientId);
        request.setRecordType(com.hackathon.ehr.enums.RecordType.valueOf(recordType));
        request.setTitle(title);
        request.setDescription(description);

        return ResponseEntity.ok(medicalRecordService.uploadMedicalRecord(request, file,
                userService.getUserByEmail(userPrincipal.getEmail())));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecords(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsForPatient(patientId,
                userService.getUserByEmail(userPrincipal.getEmail())));
    }
}
