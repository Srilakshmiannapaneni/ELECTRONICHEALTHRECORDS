package com.hackathon.ehr.service;

import com.hackathon.ehr.dto.RegisterRequest;
import com.hackathon.ehr.entity.*;
import com.hackathon.ehr.enums.RoleType;
import com.hackathon.ehr.exception.BusinessRuleException;
import com.hackathon.ehr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user, String specialization) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == RoleType.PATIENT) {
            PatientProfile profile = new PatientProfile();
            profile.setUser(savedUser);
            profile.setAge(25);
            patientProfileRepository.save(profile);
        } else if (savedUser.getRole() == RoleType.DOCTOR) {
            DoctorProfile profile = new DoctorProfile();
            profile.setUser(savedUser);
            profile.setSpecialization(specialization != null && !specialization.isBlank() ? specialization : "General");
            profile.setLicenseNumber("DOC-" + savedUser.getId());
            doctorProfileRepository.save(profile);
        }
        return savedUser;
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessRuleException("Email already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == RoleType.PATIENT) {
            if (request.getAge() == null || request.getAge() < 0 || request.getAge() > 120) {
                throw new BusinessRuleException("Patient age must be between 0 and 120.");
            }
            PatientProfile profile = new PatientProfile();
            profile.setUser(savedUser);
            profile.setAge(request.getAge());
            patientProfileRepository.save(profile);
        } else if (savedUser.getRole() == RoleType.DOCTOR) {
            if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                throw new BusinessRuleException("Doctor license number is mandatory.");
            }
            DoctorProfile profile = new DoctorProfile();
            profile.setUser(savedUser);
            profile.setSpecialization(request.getSpecialization() != null && !request.getSpecialization().isBlank() ? request.getSpecialization() : "General");
            profile.setLicenseNumber(request.getLicenseNumber());
            doctorProfileRepository.save(profile);
        }

        return savedUser;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleException("User not found."));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("User not found."));
    }

    public List<User> getUsersByRole(RoleType role) {
        return userRepository.findByRole(role);
    }

    public PatientProfile createPatientProfile(PatientProfile profile) {
        if (profile.getAge() == null || profile.getAge() < 0 || profile.getAge() > 120) {
            throw new BusinessRuleException("Patient age must be between 0 and 120.");
        }
        return patientProfileRepository.save(profile);
    }

    public DoctorProfile createDoctorProfile(DoctorProfile profile) {
        if (profile.getLicenseNumber() == null || profile.getLicenseNumber().isBlank()) {
            throw new BusinessRuleException("Doctor license number is mandatory.");
        }
        return doctorProfileRepository.save(profile);
    }
    
    public List<DoctorProfile> fetchDoctorsBySpecialization(String spec) {
        return doctorProfileRepository.findBySpecialization(spec);
    }

    public List<PatientProfile> getAllPatientProfiles() {
        return patientProfileRepository.findAll();
    }
    
    public List<DoctorProfile> getAllDoctorProfiles() {
        return doctorProfileRepository.findAll();
    }

    public PatientProfile getPatientProfileForUser(Long userId) {
        PatientProfile profile = patientProfileRepository.findByUserId(userId);
        if (profile == null) {
            throw new BusinessRuleException("Patient profile not found for current user.");
        }
        return profile;
    }

    public DoctorProfile getDoctorProfileForUser(Long userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId);
        if (profile == null) {
            throw new BusinessRuleException("Doctor profile not found for current user.");
        }
        return profile;
    }
}
