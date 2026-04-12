package com.hackathon.ehr.controller;

import com.hackathon.ehr.entity.*;
import com.hackathon.ehr.enums.RoleType;
import com.hackathon.ehr.security.UserPrincipal;
import com.hackathon.ehr.service.UserService;
import com.hackathon.ehr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user, @RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(userService.createUser(user, specialization));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        // Simple return all for UI purposes
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/patient-profiles")
    public ResponseEntity<PatientProfile> createPatient(@RequestBody PatientProfile profile) {
        return ResponseEntity.ok(userService.createPatientProfile(profile));
    }

    @GetMapping("/patient-profiles")
    public ResponseEntity<List<PatientProfile>> getPatients() {
        return ResponseEntity.ok(userService.getAllPatientProfiles());
    }

    @GetMapping("/patient-profiles/me")
    public ResponseEntity<PatientProfile> getMyPatientProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.getPatientProfileForUser(userPrincipal.getId()));
    }

    @PostMapping("/doctor-profiles")
    public ResponseEntity<DoctorProfile> createDoctor(@RequestBody DoctorProfile profile) {
        return ResponseEntity.ok(userService.createDoctorProfile(profile));
    }

    @GetMapping("/doctor-profiles")
    public ResponseEntity<List<DoctorProfile>> getDoctors() {
        return ResponseEntity.ok(userService.getAllDoctorProfiles());
    }

    @GetMapping("/doctor-profiles/me")
    public ResponseEntity<DoctorProfile> getMyDoctorProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.getDoctorProfileForUser(userPrincipal.getId()));
    }

    @GetMapping("/doctor-profiles/specialization/{specialization}")
    public ResponseEntity<List<DoctorProfile>> getDoctorsBySpec(@PathVariable String specialization) {
        return ResponseEntity.ok(userService.fetchDoctorsBySpecialization(specialization));
    }
}
