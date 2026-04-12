package com.hackathon.ehr.config;

import com.hackathon.ehr.entity.DoctorProfile;
import com.hackathon.ehr.entity.PatientProfile;
import com.hackathon.ehr.entity.User;
import com.hackathon.ehr.enums.RoleType;
import com.hackathon.ehr.repository.DoctorProfileRepository;
import com.hackathon.ehr.repository.PatientProfileRepository;
import com.hackathon.ehr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsers();
        }
    }

    private void seedUsers() {
        // Sample Patients
        createPatient("lakshmi", "lakshmi@gmail.com", "password123", 30);
        createPatient("sohi", "sohi@gmail.com", "password123", 25);
        createPatient("vysh", "vysh@gmail.com", "password123", 45);
        createPatient("nandy", "nandy@gmail.com", "password123", 35);

        // Sample Doctors with different specializations
        createDoctor("Dr. Lakshmi", "drlakshmi@gmail.com", "password123", "Cardiology", "LIC-001");
        createDoctor("Dr. Sohi", "drsohi@gmail.com", "password123", "ENT", "LIC-002");
        createDoctor("Dr. Vysh", "drvysh@gmail.com", "password123", "Neurology", "LIC-003");
        createDoctor("Dr. Nandy", "drnandy@gmail.com", "password123", "Orthopedics", "LIC-004");
        createDoctor("Dr. Siri", "drsiri@gmail.com", "password123", "Pediatrics", "LIC-005");
        createDoctor("Dr. Krish", "drkrish@gmail.com", "password123", "Dermatology", "LIC-006");
        createDoctor("Dr. Krishna", "drkrishna@gmail.com", "password123", "Gynecology", "LIC-007");
        createDoctor("Dr. Sonu", "drsonu@gmail.com", "password123", "Ophthalmology", "LIC-008");

        // Sample Admin
        createAdmin("Admin", "admin@gmail.com", "admin123");

        // Sample Pharmacist
        createPharmacist("Pharmacist", "pharmacist@gmail.com", "password123");
    }

    private void createPatient(String name, String email, String password, int age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(RoleType.PATIENT);
        User savedUser = userRepository.save(user);

        PatientProfile profile = new PatientProfile();
        profile.setUser(savedUser);
        profile.setAge(age);
        patientProfileRepository.save(profile);
    }

    private void createDoctor(String name, String email, String password, String specialization, String licenseNumber) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(RoleType.DOCTOR);
        User savedUser = userRepository.save(user);

        DoctorProfile profile = new DoctorProfile();
        profile.setUser(savedUser);
        profile.setSpecialization(specialization);
        profile.setLicenseNumber(licenseNumber);
        profile.setConsultationFee(100.0); // Default fee
        profile.setYearsOfExperience(5);
        profile.setRating(4.5);
        doctorProfileRepository.save(profile);
    }

    private void createAdmin(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);
    }

    private void createPharmacist(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(RoleType.PHARMACIST);
        userRepository.save(user);
    }
}
