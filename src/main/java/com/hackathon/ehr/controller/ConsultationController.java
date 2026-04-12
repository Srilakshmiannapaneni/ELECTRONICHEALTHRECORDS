package com.hackathon.ehr.controller;

import com.hackathon.ehr.dto.ConsultationRequest;
import com.hackathon.ehr.entity.Consultation;
import com.hackathon.ehr.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<Consultation> bookConsultation(@RequestBody ConsultationRequest request) {
        return ResponseEntity.ok(consultationService.bookConsultation(request));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Consultation> startConsultation(@PathVariable Long id, @RequestParam Long doctorId) {
        return ResponseEntity.ok(consultationService.startConsultation(id, doctorId));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Consultation> completeConsultation(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(consultationService.completeConsultation(id, payload.get("diagnosis"), payload.get("notes")));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Consultation>> getPatientConsultations(@PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationsForPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Consultation>> getDoctorConsultations(@PathVariable Long doctorId) {
        return ResponseEntity.ok(consultationService.getConsultationsForDoctor(doctorId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Consultation> cancelConsultation(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.cancelConsultation(id));
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<Consultation> markNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.markNoShow(id));
    }

    @GetMapping
    public ResponseEntity<List<Consultation>> getAllConsultations() {
        // Mock method to easily fetch everything for UI logic in simple hackathon project
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }
}
