package com.hackathon.ehr.controller;

import com.hackathon.ehr.dto.PrescriptionRequest;
import com.hackathon.ehr.entity.Prescription;
import com.hackathon.ehr.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Prescription> issuePrescription(@RequestParam Long consultationId, @RequestBody PrescriptionRequest request) {
        request.setConsultationId(consultationId);
        return ResponseEntity.ok(prescriptionService.issuePrescription(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getPatientPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPatientPrescriptions(patientId));
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @PutMapping("/{id}/dispense")
    public ResponseEntity<Prescription> dispensePrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.dispensePrescription(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Prescription> rejectPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.rejectPrescription(id));
    }
}
