package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    PatientProfile findByUserId(Long userId);
}
