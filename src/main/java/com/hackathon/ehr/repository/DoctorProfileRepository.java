package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    DoctorProfile findByUserId(Long userId);
    List<DoctorProfile> findBySpecialization(String specialization);
}
