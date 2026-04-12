package com.hackathon.ehr.controller;

import com.hackathon.ehr.entity.Consultation;
import com.hackathon.ehr.entity.User;
import com.hackathon.ehr.enums.RoleType;
import com.hackathon.ehr.repository.UserRepository;
import com.hackathon.ehr.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ConsultationService consultationService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) RoleType role) {
        if (role == null) {
            return ResponseEntity.ok(userRepository.findAll());
        }
        return ResponseEntity.ok(userRepository.findByRole(role));
    }

    @GetMapping("/consultations")
    public ResponseEntity<List<Consultation>> getConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }
}
